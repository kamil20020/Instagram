package pl.instagram.instagram.controller;

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

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MDIyNTI2LCJleHAiOjE3MjUxMDg5MjYsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.VNhqoXGTEDOsXmLelUkE8MJXvs_Uoa0HvKKANbpE8uLjFZsx1AyJYJxmK0CFtTsm0WsPDKeLzKxF1HPjF0AAbSxzMIXOvbAVMP1O0C6acFb6EudcXEHzmWZzCYNVnjAUyW0oFI4ZKWi5b1ZipI8SHNAv62No8sFD7R3k1NWUtAKvctd2hh64Q0GACC6sGyD7CXRHCsqTrAwdEYKxnpWdf6-We2jI2zRJR1f4unFDPJ3jUc_pG392HJOkr-KidonfVFRVtkLGi_WFSiuZkRKpvSomVlQjRDoFbaGz7xWzpRmYour6f5eyI9gtEJtjdCLLXs5TsM-lECBSKRSAGhAEKQ";

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