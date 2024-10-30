package pl.instagram.instagram.controller.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.CommentLikeController;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.CommentLikeService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldGetCommentLikesPage() throws Exception {

        //given
        String userIdStr = "c08e2433-6876-4132-9bd6-6ef4e3f23327";
        String commentIdStr = "00f897f2-e01f-4b8e-bcab-26f10782468c";
        UUID userId = UUID.fromString(userIdStr);
        UUID commentId = UUID.fromString(commentIdStr);
        Pageable pageable = PageRequest.of(0, 5);

        UserEntity like = UserEntity.builder()
            .id(userId)
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
            .build();

        UserHeader likeHeader = new UserHeader(
            userIdStr,
            like.getNickname(),
            like.getFirstname(),
            like.getSurname(),
            null,
            like.isVerified()
        );

        Page<UserEntity> likes = new PageImpl<>(List.of(like));
        Page<UserHeader> likesHeaders = new PageImpl<>(List.of(likeHeader));

        //when
        Mockito.when(uuidMapper.strToUUID(any(), any())).thenReturn(commentId);
        Mockito.when(commentLikeService.getCommentLikesPage(any(), any())).thenReturn(likes);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(likeHeader);

        MvcResult mvcResult = mockMvc
            .perform(
                get(URL_PREFIX + commentIdStr + "/likes")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResult = mvcResult.getResponse().getContentAsString();
        Page<UserHeader> gotLikesHeadersPage = objectMapper.readValue(jsonResult, RestPage.class);
        List<UserHeader> gotLikesHeaders = objectMapper.convertValue(gotLikesHeadersPage.getContent(), new TypeReference<List<UserHeader>>(){});

        //then
        assertEquals(1, gotLikesHeaders.size());
        assertEquals(likeHeader, gotLikesHeaders.get(0));
        
        Mockito.verify(uuidMapper).strToUUID(eq(commentIdStr), anyString());
        Mockito.verify(commentLikeService).getCommentLikesPage(commentId, pageable);
        Mockito.verify(userMapper).userEntityToUserHeader(like);
    }
}
