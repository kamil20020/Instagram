package pl.instagram.instagram.controller.unit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.CommentLikeController;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.service.CommentLikeService;

@WebMvcTest(value = CommentLikeController.class)
@Import(SecurityConfig.class)
public class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentLikeService commentLikeService;

    @MockBean
    private UUIDMapper uuidMapper;

    @MockBean
    private UserMapper userMapper;

    private static final String URL_PREFIX = "/comments/";

    @Test
    public void shouldGetCommentLikesPage(){

        //given
        
        //when

        //then
    }
}
