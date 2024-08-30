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
import pl.instagram.instagram.model.api.response.FollowersResponse;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.FollowerEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.FollowerRepository;
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
class FollowerControllerTestIT {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private PostRepository postRepository;

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MDQ3MTA1LCJleHAiOjE3MjUxMzM1MDUsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.BItftyrKWzpSVm15EN3L5QQisXnQV2_5enhJ8dNnAg3HLvotfnMQX3kXnJE7XHxSFML3YEAqf1tWcT4qPikmgC61NcHdSIRkOOk10jixl9XkSQi7-4C5Vv6alOxaMuzKjx0_MChnd6PGdfm8jv9RC1niQXqjfuZxahfhFg6x-hjC1oeLAhmK6T--g4IZkOzuGTlYl940jnwbGXDzwZSeRQfwOeL-fEixVwlbFPuv5iuUSvOAFcdTNh8o_ziKTPE1pTvQa8fHkh3Oew_nnlsK0aQmI1GGARAiGOi_k30cH1jHJlZQwiM69BHNvSRhRJbp4JU5Mq858z_lMbIi1TuZ7w";

    @BeforeEach
    private void setUp(){

        RestAssured.baseURI = "http://localhost/users/";
        RestAssured.port = port;

        userRepository.deleteAll();
        followerRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldGetFollowersPage() {

        //given
        UserEntity followed = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(0)
            .numberOfPosts(0)
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .build();

        followed = userRepository.save(followed);

        UserEntity follower = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(1)
            .numberOfPosts(0)
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .build();

        follower = userRepository.save(follower);

        FollowerEntity followerEntity = new FollowerEntity(followed, follower);

        followerEntity = followerRepository.save(followerEntity);

        follower.getFollowedUsers().add(followerEntity);
        followed.getFollowersUsers().add(followerEntity);

        follower = userRepository.save(follower);
        followed = userRepository.save(followed);

        //when
        FollowersResponse gotFollowerResponse = given()
            .param("page", 0)
            .param("size", 5)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .get("{followedId}/followers", followed.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(FollowersResponse.class);

        Page<UserHeader> gotFollowersHeadersPage = gotFollowerResponse.followers();
        UserHeader gotFollowerHeader = gotFollowersHeadersPage.getContent().get(0);

        //then
        assertEquals(1, gotFollowersHeadersPage.getTotalElements());
        assertEquals(follower.getId().toString(), gotFollowerHeader.id());
        assertEquals(follower.getNickname(), gotFollowerHeader.nickname());
        assertEquals(follower.getFirstname(), gotFollowerHeader.firstname());
        assertEquals(follower.getSurname(), gotFollowerHeader.surname());
        assertEquals(follower.isVerified(), gotFollowerHeader.isVerified());
        assertTrue(gotFollowerResponse.didLoggedUserFollowed());
    }

    @Test
    void shouldGetFollowedPage() {

        //given
        UserEntity followed = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(0)
            .numberOfPosts(0)
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .build();

        followed = userRepository.save(followed);

        UserEntity follower = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(1)
            .numberOfPosts(0)
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .build();

        follower = userRepository.save(follower);

        FollowerEntity followerEntity = new FollowerEntity(followed, follower);

        followerEntity = followerRepository.save(followerEntity);

        follower.getFollowedUsers().add(followerEntity);
        followed.getFollowersUsers().add(followerEntity);

        follower = userRepository.save(follower);
        followed = userRepository.save(followed);

        //when
        Page<UserHeader> gotFollowedHeadersPage = given()
            .param("page", 0)
            .param("size", 5)
        .when()
            .get("{followerId}/followed", follower.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<UserHeader>>(){});

        UserHeader gotFollowedHeader = gotFollowedHeadersPage.getContent().get(0);

        //then
        assertEquals(1, gotFollowedHeadersPage.getTotalElements());
        assertEquals(followed.getId().toString(), gotFollowedHeader.id());
        assertEquals(followed.getNickname(), gotFollowedHeader.nickname());
        assertEquals(followed.getFirstname(), gotFollowedHeader.firstname());
        assertEquals(followed.getSurname(), gotFollowedHeader.surname());
        assertEquals(followed.isVerified(), gotFollowedHeader.isVerified());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    public void shouldGetPostsByFollowedUsers(){

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(1)
            .followings(0)
            .followers(1)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        UserEntity loggedUser = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .nickname("kamil1")
            .firstname("Kamil1")
            .surname("Kowalski1")
            .creationDatetime(LocalDateTime.now())
            .numberOfPosts(0)
            .followings(1)
            .followers(0)
            .isVerified(true)
            .isPrivate(false)
            .posts(new HashSet<>())
            .followedUsers(new HashSet<>())
            .followersUsers(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        loggedUser = userRepository.save(loggedUser);

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
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        FollowerEntity follower = new FollowerEntity(author, loggedUser);
        follower = followerRepository.save(follower);

        author.getFollowersUsers().add(follower);
        loggedUser.getFollowedUsers().add(follower);

        author = userRepository.save(author);
        loggedUser = userRepository.save(loggedUser);

        //when
        Page<PostDetails> gotPosts = given()
            .param("page", 0)
            .param("size", 5)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .get("/followed/posts")
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<PostDetails>>(){});

        PostDetails gotPostDetails = gotPosts.getContent().get(0);

        //then
        assertEquals(1, gotPosts.getTotalElements());
        assertEquals(post.getId().toString(), gotPostDetails.id());
        assertEquals(post.getDescription(), gotPostDetails.description());
        assertEquals(encodedContent, gotPostDetails.content());
        assertEquals(post.isAreHiddenLikes(), gotPostDetails.areHiddenLikes());
        assertEquals(post.isAreDisabledComments(), gotPostDetails.areDisabledComments());
        assertEquals(post.getCreationDatetime(), gotPostDetails.creationDatetime().toLocalDateTime());
        assertEquals(post.getLikesCount(), gotPostDetails.likesCount());
        assertEquals(post.getCommentsCount(), gotPostDetails.commentsCount());
        assertEquals(author.getId().toString(), gotPostDetails.author().id());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    public void shouldCreateFollow(){

        //given
        UserEntity followed = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(0)
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .build();

        followed = userRepository.save(followed);

        UserEntity follower = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(0)
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .build();

        follower = userRepository.save(follower);

        //when
        UserHeader gotFollower = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .post("{followedId}/followers", followed.getId())
        .then()
            .statusCode(201)
            .extract()
            .as(UserHeader.class);

        followed = userRepository.findById(followed.getId()).orElseThrow();
        follower = userRepository.findById(follower.getId()).orElseThrow();

        FollowerEntity.FollowerEntityId followerEntityId = new FollowerEntity.FollowerEntityId(followed.getId(), follower.getId());

        //then
        assertTrue(followerRepository.existsById(followerEntityId));
        assertEquals(1, followed.getFollowers());
        assertEquals(1, follower.getFollowings());

        assertEquals(follower.getId().toString(), gotFollower.id());
    }

    @Test
    public void shouldDeleteFollow(){

        //given
        UserEntity followed = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(0)
            .numberOfPosts(0)
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .build();

        followed = userRepository.save(followed);

        UserEntity follower = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(1)
            .numberOfPosts(0)
            .comments(new HashSet<>())
            .posts(new HashSet<>())
            .followersUsers(new HashSet<>())
            .followedUsers(new HashSet<>())
            .build();

        follower = userRepository.save(follower);

        FollowerEntity followerEntity = new FollowerEntity(followed, follower);
        followerEntity = followerRepository.save(followerEntity);

        followed.getFollowersUsers().add(followerEntity);
        follower.getFollowedUsers().add(followerEntity);

        followed = userRepository.save(followed);
        follower = userRepository.save(follower);

        //when
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
        .when()
            .delete("{followedId}/followers", followed.getId())
        .then()
            .statusCode(204);

        followed = userRepository.findById(followed.getId()).orElseThrow();
        follower = userRepository.findById(follower.getId()).orElseThrow();

        //then
        assertFalse(followerRepository.existsById(followerEntity.getId()));
        assertEquals(0, followed.getFollowers());
        assertEquals(0, follower.getFollowings());
    }
}