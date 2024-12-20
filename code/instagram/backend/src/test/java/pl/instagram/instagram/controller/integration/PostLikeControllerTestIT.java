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

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwSFlTYkVhVFpKa3NtN09TcDNYMEtsT2d1RmNETUhBWEBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzMwMjE0MjE4LCJleHAiOjE3MzAzMDA2MTgsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6InBIWVNiRWFUWkprc203T1NwM1gwS2xPZ3VGY0RNSEFYIn0.d1nIs3Yh3BPZ_Msxk9dRJx54AQj8cT8BVFZ8iWNh8EqA1M5h-PSZd_t9uT8rSWTMtjMDHELlNULj-Ns_HgFOS4J4_YeNJNGqb0y-Av9mCyEv27DmPYGR2dPIx9oHa2f4sCxWs9eNY9-iOV7FuMi0xyjqZYiJu5zGQnm3kMEDL_ztfUFBKwk6l3sL_l0KSLMY9vRltpPUgxj40EYAlrpJ0hEFeLpa27j2Hgs9Cc_NLqcPZnlNI5tFW9GSA8O3Uh1Azo-drjyLRoZ970035dDyNhIMy79xklLnV0yezpeptCGNZ21PVFmwtgQWD11prlMi1TQzskgjigUtLDo5Dr8sxg";
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