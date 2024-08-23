package pl.instagram.instagram.controller.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.request.PatchPost;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostControllerTestIT {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.0");

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UUIDMapper uuidMapper;

    private static final String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI0NDI1Mzg0LCJleHAiOjE3MjQ1MTE3ODQsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.wGsRxh52w__PE3p1PN0VVT9x_xAfuIk9gHb3NYkYTz6k6FCu-vbvcSuh1jGj5Tq_W91Tu1NLF6J9E0OX6zxDKLOLWEXuFw_jZI8nhp43ddpp8PEWUsDDypTkg1Li_nJqkUtFwrGqx8o9LV29CAfII6_l5HbAwsTCBlZod1SN61WX7U8NWNQ1Hq6Wc0_E-u-eDVj_c2uwMlRtkOPXkNCbtGvx0_k5z-acLwz6FrYGCP04rId9xZX6y3CGBlm-ELxu2UMMjQDD5rsMv0EuA2uk1mWDARyoLN9U-mU6DxAEyT-jen39byHD8Ph6o2Z2k1mp7BCihM4ldAiivAENNyw0GA";

    @LocalServerPort
    private Integer port;

    @BeforeEach
    private void setUp(){

        RestAssured.baseURI = "http://localhost/posts";
        RestAssured.port = port;

        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void shouldGetPostById() {

        UserEntity author = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .content(content)
            .creationDatetime(LocalDateTime.now())
            .commentsCount(0)
            .likesCount(0)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(author)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        PostDetails gotPostDetails = given()
        .when()
            .get("/{id}", post.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(PostDetails.class);

        assertEquals(post.getId().toString(), gotPostDetails.id());
        assertEquals(post.getDescription(), gotPostDetails.description());
        assertEquals(encodedContent, gotPostDetails.content());
        assertEquals(post.getCreationDatetime(), gotPostDetails.creationDatetime().toLocalDateTime());
        assertEquals(post.getCommentsCount(), gotPostDetails.commentsCount());
        assertEquals(post.getLikesCount(), gotPostDetails.likesCount());
        assertEquals(post.isAreHiddenLikes(), gotPostDetails.areHiddenLikes());
        assertEquals(post.isAreDisabledComments(), gotPostDetails.areDisabledComments());

        UserHeader gotUser = gotPostDetails.author();

        assertNotNull(gotUser);
        assertEquals(author.getId().toString(), gotUser.id());
        assertEquals(author.getNickname(), gotUser.nickname());
        assertEquals(author.getFirstname(), gotUser.firstname());
        assertEquals(author.getSurname(), gotUser.surname());
        assertEquals(author.isVerified(), gotUser.isVerified());
    }

    @Test
    void shouldGetUserPostsHeadersPage() {

        UserEntity author = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        UserEntity author1 = UserEntity.builder()
            .accountId("A1")
            .nickname("kamil1")
            .firstname("Kamil1")
            .surname("Kowalski1")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);
        author1 = userRepository.save(author1);

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .content(content)
            .creationDatetime(LocalDateTime.now())
            .commentsCount(0)
            .likesCount(0)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(author)
            .build();

        byte[] content1 = ("Zawartość postu1").getBytes(StandardCharsets.UTF_8);
        String encodedContent1 = "WmF3YXJ0b8WbxIcgcG9zdHUx";

        PostEntity post1 = PostEntity.builder()
            .description("Opis postu1")
            .content(content1)
            .creationDatetime(LocalDateTime.now())
            .commentsCount(0)
            .likesCount(0)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(author1)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);
        author1.getPosts().add(post1);

        Page<PostHeader> gotPostsHeadersPage = given()
            .param("page", 0)
            .param("size", 5)
        .when()
            .get("/author/{authorId}", author.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<PostHeader>>(){});

        PostHeader gotPost = gotPostsHeadersPage.getContent().get(0);

        assertEquals(1, gotPostsHeadersPage.getTotalElements());
        assertEquals(post.getId().toString(), gotPost.id());
        assertEquals(encodedContent, gotPost.content());
        assertEquals(post.getLikesCount(), gotPost.likesCount());
        assertEquals(post.getCommentsCount(), gotPost.commentsCount());
        assertEquals(post.isAreHiddenLikes(), gotPost.areHiddenLikes());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldCreatePost() {

        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        CreatePost createPost = new CreatePost(
            "Opis postu",
            encodedContent,
            true,
            false
        );

        PostDetails gotPost = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(createPost)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(201)
            .extract()
            .as(PostDetails.class);

        UUID gotPostId = uuidMapper.strToUUID(gotPost.id(), "");

        assertNotNull(gotPost);;
        assertTrue(postRepository.existsById(gotPostId));
        assertEquals(createPost.description(), gotPost.description());
        assertEquals(createPost.content(), gotPost.content());
        assertEquals(createPost.areHiddenLikes(), gotPost.areHiddenLikes());
        assertEquals(createPost.areDisabledComments(), gotPost.areDisabledComments());
        assertNotNull(gotPost.author());
        assertEquals(author.getId().toString(), gotPost.author().id());
    }

    @Test
    void shouldNotCreatePostWithUnloggedUser() {

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        CreatePost createPost = new CreatePost(
            "Opis postu",
            encodedContent,
            true,
            false
        );

        given()
            .body(createPost)
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(401);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldPatchPostById() {

        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .content(content)
            .creationDatetime(LocalDateTime.now())
            .commentsCount(0)
            .likesCount(0)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(author)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        PatchPost patchPost = new PatchPost(
            "Opis postu1",
            true,
            false
        );

        PostDetails gotPost = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .body(patchPost)
            .contentType(ContentType.JSON)
        .when()
            .patch("/{postId}", post.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(PostDetails.class);

        PostEntity foundPost = postRepository.findById(post.getId()).get();

        assertEquals(post.getId(), foundPost.getId());
        assertEquals(patchPost.description(), foundPost.getDescription());
        assertEquals(patchPost.areDisabledComments(), foundPost.isAreDisabledComments());
        assertEquals(patchPost.areHiddenLikes(), foundPost.isAreHiddenLikes());

        assertEquals(post.getId().toString(), gotPost.id());
        assertEquals(patchPost.description(), gotPost.description());
        assertEquals(patchPost.areDisabledComments(), gotPost.areDisabledComments());
        assertEquals(patchPost.areHiddenLikes(), gotPost.areHiddenLikes());
    }

    @Test
    void shouldNotPatchPostByIdWithUnloggedUser() {

        UUID postId = UUID.randomUUID();

        PatchPost patchPost = new PatchPost(
            "Opis postu1",
            true,
            false
        );

        given()
            .body(patchPost)
            .contentType(ContentType.JSON)
        .when()
            .patch("/{id}", postId)
        .then()
            .statusCode(401);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldDeletePostById() {

        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        byte[] content = ("Zawartość postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b8WbxIcgcG9zdHU=";

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .content(content)
            .creationDatetime(LocalDateTime.now())
            .commentsCount(0)
            .likesCount(0)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(author)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .when()
            .delete("/{postId}", post.getId())
        .then()
            .statusCode(204);

        assertFalse(postRepository.existsById(post.getId()));
        assertEquals(1, userRepository.findById(author.getId()).get().getNumberOfPosts());
    }

    @Test
    void shouldNotDeletePostByIdWhenUserIsNotLogged() {

        UUID postId = UUID.randomUUID();

        given()
        .when()
            .delete("/{postId}", postId)
        .then()
            .statusCode(401);
    }
}