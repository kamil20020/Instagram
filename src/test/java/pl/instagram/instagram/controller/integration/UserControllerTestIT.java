package pl.instagram.instagram.controller.integration;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.net.MediaType;
import pl.instagram.instagram.controller.UserController;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTestIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void setUp(){

        RestAssured.baseURI = "http://localhost/users";
        RestAssured.port = port;

        userRepository.deleteAll();
    }

    @Test
    void shouldGetUserHeaderById() {

        byte[] avatar = ("kamil").getBytes();
        String encodedAvatar = "a2FtaWw=";

        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatar)
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .isVerified(true)
            .build();

        u1 = userRepository.save(u1);

        UserHeader userHeader = when()
            .get("/{id}/header", u1.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(UserHeader.class);

        assertEquals(u1.getId().toString(), userHeader.id());
        assertEquals(u1.getNickname(), userHeader.nickname());
        assertEquals(u1.getFirstname(), userHeader.firstname());
        assertEquals(u1.getSurname(), userHeader.surname());
        assertEquals(encodedAvatar, userHeader.avatar());
        assertEquals(u1.isVerified(), userHeader.isVerified());
    }

    @Test
    void shouldGetUserProfile() {

        byte[] avatar = ("kamil").getBytes();
        String encodedAvatar = "a2FtaWw=";

        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatar)
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .isVerified(true)
            .isPrivate(true)
            .build();

        u1 = userRepository.save(u1);

        UserProfile userProfile = when()
            .get("/{id}/profile", u1.getId())
        .then()
            .statusCode(200)
            .extract()
            .as(UserProfile.class);

        assertEquals(u1.getId().toString(), userProfile.id());
        assertEquals(u1.getNickname(), userProfile.nickname());
        assertEquals(u1.getFirstname(), userProfile.firstname());
        assertEquals(u1.getSurname(), userProfile.surname());
        assertEquals(encodedAvatar, userProfile.avatar());
        assertEquals(u1.isVerified(), userProfile.isVerified());
        assertEquals(u1.isPrivate(), userProfile.isPrivate());
        assertEquals(u1.getDescription(), userProfile.description());
        assertEquals(u1.getFollowers(), userProfile.followers());
        assertEquals(u1.getFollowings(), userProfile.followings());
        assertEquals(u1.getNumberOfPosts(), userProfile.numberOfPosts());
    }

    @Test
    void shouldGetUsersByIds() {

        List<byte[]> avatars = List.of(
            ("kamil").getBytes(StandardCharsets.UTF_8), ("michał").getBytes(StandardCharsets.UTF_8)
        );

        List<String> encodedAvatars = List.of(
            "a2FtaWw=", "bWljaGHFgg=="
        );

        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatars.get(0))
            .followings(1)
            .followers(2)
            .numberOfPosts(3)
            .isVerified(true)
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("michał")
            .firstname("Michał")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis1")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatars.get(1))
            .followings(4)
            .followers(5)
            .numberOfPosts(6)
            .isVerified(false)
            .build();

        List<UserEntity> createdUsers = userRepository.saveAll(List.of(u1, u2));
        u1 = createdUsers.get(0);
        u2 = createdUsers.get(1);

        UserHeader[] usersHeaders = given()
            .queryParam("ids", List.of(u1.getId(), u2.getId()))
        .when()
            .get("/ids")
        .then()
            .statusCode(200)
            .extract()
            .as(UserHeader[].class);

        for(int i = 0; i < createdUsers.size(); i++){

            UserEntity user = createdUsers.get(i);
            UserHeader userHeader = usersHeaders[i];
            String encodedAvatar = encodedAvatars.get(i);

            assertEquals(user.getId().toString(), userHeader.id());
            assertEquals(user.getNickname(), userHeader.nickname());
            assertEquals(user.getFirstname(), userHeader.firstname());
            assertEquals(user.getSurname(), userHeader.surname());
            assertEquals(encodedAvatar, userHeader.avatar());
            assertEquals(user.isVerified(), userHeader.isVerified());
        }
    }

    @Test
    void shouldSearchUsers() {

        List<byte[]> avatars = List.of(
            ("kamil").getBytes(StandardCharsets.UTF_8), ("michał").getBytes(StandardCharsets.UTF_8)
        );

        List<String> encodedAvatars = List.of(
            "a2FtaWw=", "bWljaGHFgg=="
        );

        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatars.get(0))
            .followings(1)
            .followers(2)
            .numberOfPosts(3)
            .isVerified(true)
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("michał")
            .firstname("Michał")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis1")
            .creationDatetime(LocalDateTime.now())
            .avatar(avatars.get(1))
            .followings(4)
            .followers(5)
            .numberOfPosts(6)
            .isVerified(false)
            .build();

        List<UserEntity> createdUsers = userRepository.saveAll(List.of(u1, u2));
        u1 = createdUsers.get(0);
        u2 = createdUsers.get(1);

        Page<UserHeader> usersHeadersPage = given()
            .queryParam("phrase", "ami wa")
            .queryParam("page", 0)
            .queryParam("size", 5)
        .when()
            .get()
        .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<RestPage<UserHeader>>(){});

        UserHeader gotUser = usersHeadersPage.getContent().get(0);

        assertEquals(1, usersHeadersPage.getTotalElements());
        assertEquals(u1.getId().toString(), gotUser.id());
        assertEquals(u1.getNickname(), gotUser.nickname());
        assertEquals(u1.getFirstname(), gotUser.firstname());
        assertEquals(u1.getSurname(), gotUser.surname());
        assertEquals(encodedAvatars.get(0), gotUser.avatar());
        assertEquals(u1.isVerified(), gotUser.isVerified());
    }

    @Test
    void shouldRegisterUser() {

        String accountId = "A";

        String gotIdStr = given()
            .queryParam("accountId", accountId)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .extract()
            .asString();

        assertNotNull(UUID.fromString(gotIdStr));
        assertTrue(userRepository.existsByAccountId(accountId));
    }

    // It is necessary to manually replace accessToken variable with a new token generated from auth0 e.g. using Postman
    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients")
    void shouldFillPersonalData() throws UnirestException {

        String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI0MDA1NjcwLCJleHAiOjE3MjQwOTIwNzAsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.d5W9oyYU0_3r6I5yVOzFatKoYzxNR0kWxUHKXz6NyDLB24iK3SGIGuFew1Xvfo8n5jNrCblqAws--0WpluSiISoqWU-dhzLRZQDMucJS8Nj9TjFNKTJpNk1xcpPeBXeRreKT744QdPP4-ZcfJVVC1bmeRU62UWjxTj417O41fRvw3nj0dcZnoQbtTMIovqilojf1a3Jdqf386wbazD_RRHWuBcbomnf6EF4H0bVRNIogVEtskYqEON2Lj5DMvwEK13vbIG_HDkZZsL5qru9FhC7sc8gv_wxmApjhMd7FTPsBZgQK7jOOjsujvZSGZDsoRvsEtpokJLCtbX3yutPAYQ";
        String accountId = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji@clients";

        UserEntity u1 = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .followings(4)
            .followers(5)
            .numberOfPosts(6)
            .build();

        u1 = userRepository.save(u1);

        PersonalData personalData = new PersonalData(
            "Kamil",
            "Kowalski",
            "kamil"
        );

        UserHeader updatedUser = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) //getAccessToken()
            .contentType(ContentType.JSON)
            .body(personalData)
        .when()
            .post("/fill-personal-data")
        .then()
                .statusCode(200)
            .extract()
            .as(UserHeader.class);

        assertEquals(personalData.firstname(), updatedUser.firstname());
        assertEquals(personalData.surname(), updatedUser.surname());
        assertEquals(personalData.nickname(), updatedUser.nickname());

        u1 = userRepository.findById(UUID.fromString(updatedUser.id())).get();

        assertEquals(u1.getId().toString(), updatedUser.id());
        assertEquals(personalData.firstname(), u1.getFirstname());
        assertEquals(personalData.surname(), u1.getSurname());
        assertEquals(personalData.nickname(), u1.getNickname());
    }
}