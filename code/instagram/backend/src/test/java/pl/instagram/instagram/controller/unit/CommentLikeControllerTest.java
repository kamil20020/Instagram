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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.CommentLikeController;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.CommentLikeService;

import java.net.URL;
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

    private static final String AUTH_USERNAME = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji";

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwSFlTYkVhVFpKa3NtN09TcDNYMEtsT2d1RmNETUhBWEBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzMwNTAwMzI3LCJleHAiOjE3MzA1ODY3MjcsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6InBIWVNiRWFUWkprc203T1NwM1gwS2xPZ3VGY0RNSEFYIn0.Hpuiwy3gCVrNQnPxFSkGOG87FphVUXOe-uCPIqGK8_vOFL8HxZC7kycbWvaaUDdin_T3U_1XhCiWcoFbACcjFusfmg-G7aZwYiNuwqQ-2VEaG6lPoJFSVTXf-qCRnP2La0k7hGJ6efUeusWizSs_BsMFG8Jk63nsdYc6uxigiC6adHioZ6hAYTWqCmZSc6SUCp_FXpmHC8hffzaE5q2ynZHUZBu0O-lDhLQk3a_wWt3z4uDdcP27ZLMCldzwfxi92SaHPymS3vlBCH2S3p7pkkWlB99UK-8kNgIZh4Yv7DldALxbmcn3W45je9sWAzbVp-q1hTgy7UPrNK1Tge1geQ";

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

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    public void shouldDeleteCommentLike() throws Exception {

        //given
        String commentIdStr = "00f897f2-e01f-4b8e-bcab-26f10782468c";
        UUID commentId = UUID.fromString(commentIdStr);

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(commentId);

        mockMvc
            .perform(
                delete(URL_PREFIX + commentIdStr + "/likes")
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
            )
        .andDo(print())
        .andExpect(status().isNoContent());

        //then
        Mockito.verify(uuidMapper).strToUUID(eq(commentIdStr), anyString());
        Mockito.verify(commentLikeService).deleteCommentLike(commentId);
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    public void shouldNotDeleteNotExistingCommentLike() throws Exception {

        //given
        String commentIdStr = "00f897f2-e01f-4b8e-bcab-26f10782468c";
        UUID commentId = UUID.fromString(commentIdStr);

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(commentId);
        Mockito.doThrow(EntityNotFoundException.class).when(commentLikeService).deleteCommentLike(commentId);

        mockMvc
            .perform(
                delete(URL_PREFIX + commentIdStr + "/likes")
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(uuidMapper).strToUUID(eq(commentIdStr), anyString());
        Mockito.verify(commentLikeService).deleteCommentLike(commentId);
    }

    @Test
    @WithMockUser(username = AUTH_USERNAME)
    public void shouldCreateCommentLike(){

        //given

        //when

        //then
    }
}
