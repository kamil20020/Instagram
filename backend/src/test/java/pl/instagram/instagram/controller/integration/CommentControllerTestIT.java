package pl.instagram.instagram.controller.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import jakarta.transaction.Transactional;
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
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentControllerTestIT {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @LocalServerPort
    private Integer port;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UUIDMapper uuidMapper;

    @Autowired
    private CommentMapper commentMapper;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MjgyNDQzLCJleHAiOjE3MjUzNjg4NDMsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.e28BKPBXPlyJOHTIwhYcesiCThvqv8SZe-ItJcX5fHXbd98ctY6afVh-naDn-sCFhiP6WXSGMwNjHGKBNTVrLs0hft18iZGTswq8zy-N381UB_BOjX79YCjTr9Ji6gbV0Hn1rsdDhKzofpDX0irEkrN2gcqoMt98zRGmo0Io91eggdnf9ED3ljFBfVDqiifymBWujg44JuJusEPExRu8nDfmGt2eN2kQFL_LKtEgDeqWyvWLXfYg-suvL9RBTWa2d2tRY15Fh9ofg7yX68GU53Ojyi40TuNDal36iVHH9EmQ5wCND7esbnjOxKYyW1Ewl4qeRW3kd4ahzeeIr494wQ";

    @BeforeEach
    private void setUp(){

        RestAssured.baseURI = "http://localhost/posts/";
        RestAssured.port = port;

        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNull() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(1)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        CommentEntity c1 = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(1)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        c1 = commentRepository.save(c1);

        author.getComments().add(c1);
        post.getComments().add(c1);

        //when
        Page<CommentData> gotCommentDataPage = given()
            .param("page", 0)
            .param("size", 5)
        .when()
            .get("{postId}/comments", post.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<CommentData>>(){});

        CommentData gotCommentData = gotCommentDataPage.getContent().get(0);

        //then
        assertEquals(1, gotCommentDataPage.getTotalElements());
        assertEquals(c1.getAuthor().getId().toString(), gotCommentData.author().id());
        assertEquals(c1.getId().toString(), gotCommentData.id());
        assertEquals(c1.getContent(), gotCommentData.content());
        assertEquals(c1.getCreationDatetime(), gotCommentData.creationDatetime().toLocalDateTime());
        assertEquals(c1.getSubCommentsCount(), gotCommentData.subCommentsCount());
        assertEquals(c1.getLikesCount(), gotCommentData.likesCount());
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNotNull() {

        //given
        UserEntity author = UserEntity.builder()
                .accountId("A")
                .creationDatetime(LocalDateTime.now())
                .isVerified(true)
                .isPrivate(false)
                .followers(1)
                .followings(2)
                .numberOfPosts(1)
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis postu")
                .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
                .areHiddenLikes(true)
                .areDisabledComments(false)
                .likesCount(1)
                .commentsCount(2)
                .author(author)
                .comments(new HashSet<>())
                .build();

        post = postRepository.save(post);

        CommentEntity parentComment = CommentEntity.builder()
                .content("Dobre zdjęcie")
                .creationDatetime(LocalDateTime.now())
                .likesCount(1)
                .subCommentsCount(1)
                .subComments(new HashSet<>())
                .post(post)
                .author(author)
                .build();

        parentComment = commentRepository.save(parentComment);

        CommentEntity comment = CommentEntity.builder()
                .content("Zgadzam się")
                .creationDatetime(LocalDateTime.now())
                .likesCount(1)
                .subCommentsCount(0)
                .parentComment(parentComment)
                .post(post)
                .author(author)
                .build();

        comment = commentRepository.save(comment);

        author.getComments().addAll(List.of(parentComment, comment));
        post.getComments().addAll(List.of(parentComment, comment));
        parentComment.getSubComments().add(comment);

        //when
        Page<CommentData> gotCommentDataPage = given()
            .param("page", 0)
            .param("size", 5)
            .param("parentCommentId", parentComment.getId())
        .when()
            .get("{postId}/comments", post.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<CommentData>>(){});

        CommentData gotCommentData = gotCommentDataPage.getContent().get(0);

        //then
        assertEquals(1, gotCommentDataPage.getTotalElements());
        assertEquals(comment.getAuthor().getId().toString(), gotCommentData.author().id());
        assertEquals(comment.getId().toString(), gotCommentData.id());
        assertEquals(comment.getContent(), gotCommentData.content());
        assertEquals(comment.getCreationDatetime(), gotCommentData.creationDatetime().toLocalDateTime());
        assertEquals(comment.getSubCommentsCount(), gotCommentData.subCommentsCount());
        assertEquals(comment.getLikesCount(), gotCommentData.likesCount());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldCreatePostCommentWhenParentCommentIdIsNull() {

        //given
        UserEntity author = UserEntity.builder()
                .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
                .creationDatetime(LocalDateTime.now())
                .isVerified(true)
                .isPrivate(false)
                .followers(1)
                .followings(2)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis postu")
                .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
                .areHiddenLikes(true)
                .areDisabledComments(false)
                .likesCount(1)
                .commentsCount(0)
                .author(author)
                .comments(new HashSet<>())
                .build();

        post = postRepository.save(post);

        String content = "Dobre zdjęcie";

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        CommentData gotCommentData = given()
            .contentType(ContentType.JSON)
            .body(createComment)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .post("{postId}/comments", post.getId())
        .then()
            .statusCode(201)
            .extract()
            .as(CommentData.class);

        UUID gotCommentId = uuidMapper.strToUUID(gotCommentData.id(), "komentarza");

        //then
        assertTrue(commentRepository.existsById(gotCommentId));
        assertEquals(author.getId().toString(), gotCommentData.author().id());
        assertEquals(createComment.content(), gotCommentData.content());
        assertNotNull(gotCommentData.creationDatetime());
        assertEquals(0, gotCommentData.subCommentsCount());
        assertEquals(0, gotCommentData.likesCount());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldCreatePostCommentWhenParentCommentIdIsNotNull() {

        //given
        UserEntity author = UserEntity.builder()
                .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
                .creationDatetime(LocalDateTime.now())
                .isVerified(true)
                .isPrivate(false)
                .followers(1)
                .followings(2)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis postu")
                .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
                .areHiddenLikes(true)
                .areDisabledComments(false)
                .likesCount(1)
                .commentsCount(0)
                .author(author)
                .comments(new HashSet<>())
                .build();

        post = postRepository.save(post);

        CommentEntity parentComment = CommentEntity.builder()
                .content("Dobre zdjęcie")
                .creationDatetime(LocalDateTime.now())
                .likesCount(1)
                .subCommentsCount(0)
                .subComments(new HashSet<>())
                .post(post)
                .author(author)
                .build();

        parentComment = commentRepository.save(parentComment);

        String content = "Zgadzam się";

        CreateComment createComment = new CreateComment(
                content
        );

        //when
        CommentData gotCommentData = given()
            .contentType(ContentType.JSON)
            .body(createComment)
            .queryParam("parentCommentId", parentComment.getId())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .post("{postId}/comments", post.getId())
        .then()
            .statusCode(201)
            .extract()
            .as(CommentData.class);

        UUID gotCommentId = uuidMapper.strToUUID(gotCommentData.id(), "komentarza");

        //then
        assertTrue(commentRepository.existsById(gotCommentId));
        assertEquals(author.getId().toString(), gotCommentData.author().id());
        assertEquals(createComment.content(), gotCommentData.content());
        assertNotNull(gotCommentData.creationDatetime());
        assertEquals(0, gotCommentData.subCommentsCount());
        assertEquals(0, gotCommentData.likesCount());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldUpdateComment() {

        //given
        UserEntity author = UserEntity.builder()
                .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
                .creationDatetime(LocalDateTime.now())
                .isVerified(true)
                .isPrivate(false)
                .followers(1)
                .followings(2)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis postu")
                .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
                .areHiddenLikes(true)
                .areDisabledComments(false)
                .likesCount(1)
                .commentsCount(0)
                .author(author)
                .comments(new HashSet<>())
                .build();

        post = postRepository.save(post);

        CommentEntity comment = CommentEntity.builder()
                .content("Dobre zdjęcie")
                .creationDatetime(LocalDateTime.now())
                .likesCount(1)
                .subCommentsCount(0)
                .post(post)
                .author(author)
                .build();

        comment = commentRepository.save(comment);

        String newContent = "Dobre zdjęcie :D";

        UpdateComment updateComment = new UpdateComment(
            newContent
        );

        //when
        CommentData gotCommentData = given()
            .contentType(ContentType.JSON)
            .body(updateComment)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .put("comments/{commentId}", comment.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(CommentData.class);

        //then
        assertEquals(newContent, gotCommentData.content());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldRemoveComment() {

        //given
        UserEntity author = UserEntity.builder()
                .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
                .creationDatetime(LocalDateTime.now())
                .isVerified(true)
                .isPrivate(false)
                .followers(1)
                .followings(2)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis postu")
                .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
                .areHiddenLikes(true)
                .areDisabledComments(false)
                .likesCount(1)
                .commentsCount(0)
                .author(author)
                .comments(new HashSet<>())
                .build();

        post = postRepository.save(post);

        CommentEntity comment = CommentEntity.builder()
                .content("Dobre zdjęcie")
                .creationDatetime(LocalDateTime.now())
                .likesCount(1)
                .subCommentsCount(0)
                .post(post)
                .author(author)
                .build();

        comment = commentRepository.save(comment);

        //when
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .delete("comments/{commentId}", comment.getId())
        .then()
            .statusCode(204);

        //then
        assertFalse(commentRepository.existsById(comment.getId()));
        assertEquals(0, author.getComments().size());
        assertEquals(0, post.getCommentsCount());
        assertEquals(0, post.getComments().size());
    }
}