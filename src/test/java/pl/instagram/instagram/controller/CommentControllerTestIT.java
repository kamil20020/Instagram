package pl.instagram.instagram.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.instagram.instagram.repository.CommentRepository;

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

    @BeforeEach
    private void setUp(){

        RestAssured.baseURI = "http://localhost/posts/";
        RestAssured.port = port;

        commentRepository.deleteAll();
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNull() {


    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNotNull() {


    }

    @Test
    void shouldCreatePostCommentWhenParentCommentIdIsNull() {


    }

    @Test
    void shouldCreatePostCommentWhenParentCommentIdIsNotNull() {


    }

    @Test
    void shouldUpdateComment() {


    }

    @Test
    void shouldRemoveComment() {


    }
}