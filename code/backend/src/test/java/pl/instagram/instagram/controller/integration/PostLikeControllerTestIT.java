package pl.instagram.instagram.controller.integration;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.instagram.instagram.controller.PostLikeController;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.response.PostLikesResponse;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.PostLikeEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostLikeRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;
import pl.instagram.instagram.service.PostLikeService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostLikeControllerTestIT {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UUIDMapper uuidMapper;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MjgyNDQzLCJleHAiOjE3MjUzNjg4NDMsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.e28BKPBXPlyJOHTIwhYcesiCThvqv8SZe-ItJcX5fHXbd98ctY6afVh-naDn-sCFhiP6WXSGMwNjHGKBNTVrLs0hft18iZGTswq8zy-N381UB_BOjX79YCjTr9Ji6gbV0Hn1rsdDhKzofpDX0irEkrN2gcqoMt98zRGmo0Io91eggdnf9ED3ljFBfVDqiifymBWujg44JuJusEPExRu8nDfmGt2eN2kQFL_LKtEgDeqWyvWLXfYg-suvL9RBTWa2d2tRY15Fh9ofg7yX68GU53Ojyi40TuNDal36iVHH9EmQ5wCND7esbnjOxKYyW1Ewl4qeRW3kd4ahzeeIr494wQ";

    @BeforeEach
    private void setUp(){

        RestAssured.baseURI = "http://localhost/posts/";
        RestAssured.port = port;

        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldGetPostLikesPage() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .comments(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
            .likedComments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("A")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(0)
            .author(author)
            .postLikes(new HashSet<>())
            .build();

        post = postRepository.save(post);

        PostLikeEntity createdPostLike = postLikeRepository.save(
            new PostLikeEntity(author, post)
        );

        author.getLikedPosts().add(createdPostLike);

        post.getPostLikes().add(createdPostLike);

        author = userRepository.save(author);

        //when
        PostLikesResponse gotPostLikesResponse = given()
            .param("page", 0)
            .param("size", 5)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .get("{postId}/likes", post.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(PostLikesResponse.class);

        Page<UserHeader> gotLikesPage = gotPostLikesResponse.postLikes();
        UserHeader gotLike = gotLikesPage.getContent().get(0);

        //then
       assertEquals(1, gotLikesPage.getTotalElements());
       assertEquals(author.getId().toString(), gotLike.id());
       assertEquals(author.getNickname(), gotLike.nickname());
       assertEquals(author.getSurname(), gotLike.surname());
       assertEquals(post.getId().toString(), gotPostLikesResponse.postId());
       assertTrue(gotPostLikesResponse.didLoggedUserLikePost());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldCreatePostLike() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("A")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(0)
            .commentsCount(0)
            .author(author)
            .postLikes(new HashSet<>())
            .build();

        post = postRepository.save(post);

        //when
        UserHeader gotPostLike = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .post("{postId}/likes", post.getId())
        .then()
            .statusCode(201)
            .extract()
            .as(UserHeader.class);

        post = postRepository.findById(post.getId()).orElseThrow();

        //then
        assertTrue(postLikeRepository.existsByAuthorAccountIdAndPostId(author.getAccountId(), post.getId()));
        assertEquals(1, post.getLikesCount());
        assertEquals(author.getId().toString(), gotPostLike.id());
        assertEquals(author.getNickname(), gotPostLike.nickname());
        assertEquals(author.getSurname(), gotPostLike.surname());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldDeletePostLike() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .comments(new HashSet<>())
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .likedComments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("A")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(0)
            .author(author)
            .postLikes(new HashSet<>())
            .build();

        post = postRepository.save(post);

        PostLikeEntity createdPostLike = postLikeRepository.save(
            new PostLikeEntity(author, post)
        );

        author.getLikedPosts().add(createdPostLike);

        post.getPostLikes().add(createdPostLike);

        //when
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .delete("{postId}/likes", post.getId())
        .then()
            .statusCode(204);

        post = postRepository.findById(post.getId()).orElseThrow();

        //then
        assertFalse(postLikeRepository.existsByAuthorAccountIdAndPostId(author.getAccountId(), post.getId()));
        assertEquals(0, post.getLikesCount());
    }
}