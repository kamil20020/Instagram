package pl.instagram.instagram.controller.integration;

import io.restassured.RestAssured;
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
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentLikeControllerTestIT {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @LocalServerPort
    private Integer port;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MjA1NDA3LCJleHAiOjE3MjUyOTE4MDcsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.NzEvA8Mq14rLO2UdyVCxE7iiZPMObpuWgtPNiXtL06H_s4LGU2E7LlzL8IKZxE3C7uSZzPbj7JWJNnhpMFqmjCs_MqfuaqMa_n2xS9txgcOpkiNXUn_LZIuUre0FP3hwINrVtYkP-taLyNo7_eUvcJF7cdQesE4VwRxDuy71za4roa027ZMYvyyTc7HsaYnFGvYgzILxTunIi0hwe1YYSSiVz8F0d3QCcjhcn87y0N_NKrS48GXe4dYV2c07YrWD_2mfLfUcTNbn3Jik7915ZP5v69_TpVaA4JMsM4YI7Ev_Cj1kK-xjbuxfJQmObn6je37uZNTiMdv_CMxl3oX9cQ";

    @BeforeEach
    public void setUp(){

        RestAssured.baseURI = "http://localhost/comments/";
        RestAssured.port = port;

        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void shouldGetCommentLikesPage() {

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
            .likedComments(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Zawartość komentarza")
            .creationDatetime(LocalDateTime.now())
            .subCommentsCount(1)
            .likesCount(1)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        author.getPosts().add(post);
        author.getComments().add(comment);
        author.getLikedComments().add(comment);

        post.getComments().add(comment);

        author = userRepository.save(author);

        //when
        Page<UserHeader> gotCommentLikesPage = given()
            .param("page", 0)
            .param("size", 5)
        .when()
            .get("{commentId}/likes", comment.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<UserHeader>>(){});

        UserHeader gotCommentLike = gotCommentLikesPage.getContent().get(0);

        //then
        assertEquals(1, gotCommentLikesPage.getTotalElements());
        assertEquals(author.getId().toString(), gotCommentLike.id());
        assertEquals(author.isVerified(), gotCommentLike.isVerified());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    public void shouldCreateLike(){

        //given
        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Zawartość komentarza")
            .creationDatetime(LocalDateTime.now())
            .subCommentsCount(1)
            .likesCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        //when
        UserHeader createdLike = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .post("{commentId}/likes", comment.getId())
        .then()
            .statusCode(201)
            .extract()
            .as(UserHeader.class);

        comment = commentRepository.findById(comment.getId()).orElseThrow();

        //then
        assertTrue(userRepository.existsByAccountIdAndLikedCommentsId(author.getAccountId(), comment.getId()));
        assertEquals(1, comment.getLikesCount());

        assertEquals(author.getId().toString(), createdLike.id());
        assertEquals(author.isVerified(), createdLike.isVerified());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    public void shouldDeleteCommentLike(){

        //given
        UserEntity author = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Zawartość komentarza")
            .creationDatetime(LocalDateTime.now())
            .subCommentsCount(1)
            .likesCount(1)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        author.getPosts().add(post);
        author.getComments().add(comment);
        author.getLikedComments().add(comment);

        post.getComments().add(comment);

        author = userRepository.save(author);

        //when
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .delete("{commentId}/likes", comment.getId())
        .then()
            .statusCode(204);

        comment = commentRepository.findById(comment.getId()).orElseThrow();

        //then
        assertFalse(userRepository.existsByAccountIdAndLikedCommentsId(author.getAccountId(), comment.getId()));
        assertEquals(0, comment.getLikesCount());
    }
}