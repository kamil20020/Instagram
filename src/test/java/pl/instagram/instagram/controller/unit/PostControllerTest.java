package pl.instagram.instagram.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.PostController;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Test
    void shouldGetPostById() {


    }

    @Test
    void shouldGetUserPostsHeadersPage() {


    }

    @Test
    void shouldCreatePost() {


    }

    @Test
    void shouldPatchPostById() {


    }

    @Test
    void shouldDeletePostById() {


    }
}