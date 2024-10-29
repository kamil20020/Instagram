package pl.instagram.instagram.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.PostController;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.FormExceptionResponse;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;
import pl.instagram.instagram.mapper.PostMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.request.PatchPost;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.PatchPostData;
import pl.instagram.instagram.model.domain.PostEntityForLoggedUser;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.service.PostService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UUIDMapper uuidMapper;

    @MockBean
    private PostMapper postMapper;

    @MockBean
    private PostService postService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL_PREFIX = "/posts/";

    private static final String POST_MAPPER_MESSAGE = "posta";
    private static final String USER_MAPPER_MESSAGE = "użytkownika";

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwSFlTYkVhVFpKa3NtN09TcDNYMEtsT2d1RmNETUhBWEBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzMwMjE0MjE4LCJleHAiOjE3MzAzMDA2MTgsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6InBIWVNiRWFUWkprc203T1NwM1gwS2xPZ3VGY0RNSEFYIn0.d1nIs3Yh3BPZ_Msxk9dRJx54AQj8cT8BVFZ8iWNh8EqA1M5h-PSZd_t9uT8rSWTMtjMDHELlNULj-Ns_HgFOS4J4_YeNJNGqb0y-Av9mCyEv27DmPYGR2dPIx9oHa2f4sCxWs9eNY9-iOV7FuMi0xyjqZYiJu5zGQnm3kMEDL_ztfUFBKwk6l3sL_l0KSLMY9vRltpPUgxj40EYAlrpJ0hEFeLpa27j2Hgs9Cc_NLqcPZnlNI5tFW9GSA8O3Uh1Azo-drjyLRoZ970035dDyNhIMy79xklLnV0yezpeptCGNZ21PVFmwtgQWD11prlMi1TQzskgjigUtLDo5Dr8sxg";

    @BeforeAll
    private static void setUp(){
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldGetPostById() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        PostEntityForLoggedUser post = new PostEntityForLoggedUser();

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            true
        );

        PostDetails postDetails = new PostDetails(
            postId.toString(),
            OffsetDateTime.now(),
            "Opis postu",
            "Zawartość postu",
            true,
            false,
            userHeader,
            1,
            2
        );

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);
        Mockito.when(postService.getPostByIdForLoggedUser(any())).thenReturn(post);
        Mockito.when(postMapper.postEntityForLoggedUserToPostDetails(any())).thenReturn(postDetails);

        MvcResult mvcResult = mockMvc
            .perform(
                get(URL_PREFIX + postId)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PostDetails gotPostDetails = objectMapper.readValue(jsonResponse, PostDetails.class);

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(postService).getPostByIdForLoggedUser(postId);
        Mockito.verify(postMapper).postEntityForLoggedUserToPostDetails(post);
    }

    @Test
    void shouldNotGetPostByIdWhenInvalidPostIdWasGiven() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenThrow(IllegalArgumentException.class);

        mockMvc
            .perform(
                get(URL_PREFIX + postId)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
    }

    @Test
    void shouldNotGetPostByIdWhenPostWasNotFound() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);
        Mockito.when(postService.getPostByIdForLoggedUser(any())).thenThrow(EntityNotFoundException.class);

        mockMvc
            .perform(
                get(URL_PREFIX + postId)
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(postService).getPostByIdForLoggedUser(postId);
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
    }

    @Test
    void shouldGetUserPostsHeadersPage() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        PostEntity post = new PostEntity();

        Page<PostEntity> postsPage = new PageImpl<>(List.of(post));

        PostHeader postHeader = new PostHeader(
            postId.toString(),
            "Zawartość postu",
            1,
            3,
            true
        );

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(authorId);
        Mockito.when(postService.getUserPostsPage(any(), any())).thenReturn(postsPage);
        Mockito.when(postMapper.postEntityToPostHeader(any())).thenReturn(postHeader);

        MvcResult mvcResult = mockMvc
            .perform(
                get(URL_PREFIX + "author/" + authorId)
                .param("page", String.valueOf(0))
                .param("size", String.valueOf(5))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Page<PostHeader> gotPostsHeadersPage = objectMapper.readValue(jsonResponse, RestPage.class);

        //then
        assertEquals(postsPage.getTotalElements(), gotPostsHeadersPage.getTotalElements());

        Mockito.verify(uuidMapper).strToUUID(authorId.toString(), USER_MAPPER_MESSAGE);
        Mockito.verify(postService).getUserPostsPage(authorId, pageable);
        Mockito.verify(postMapper).postEntityToPostHeader(post);
    }

    @Test
    void shouldNotGetUserPostsHeadersPageWhenPaginationIsNotGiven() throws Exception {

        //given
        UUID authorId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(authorId);
        Mockito.when(postService.getUserPostsPage(any(), any())).thenThrow(IllegalArgumentException.class);

        mockMvc
            .perform(
                get(URL_PREFIX + "author/" + authorId)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
        Mockito.verify(uuidMapper).strToUUID(authorId.toString(), USER_MAPPER_MESSAGE);
        Mockito.verify(postService).getUserPostsPage(eq(authorId), any());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldCreatePost() throws Exception {

        //given
        CreatePost createPost = new CreatePost(
            "Opis postu",
            "Zawartość postu",
            true,
            false
        );

        PostEntity post = new PostEntity();
        PostEntity createdPost = new PostEntity();

        PostDetails postDetails = new PostDetails(
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            "Opis postu",
            "Zawartość postu",
            true,
            false,
            null,
            1,
            2
        );

        //when
        Mockito.when(postMapper.createPostToPostEntity(any())).thenReturn(post);
        Mockito.when(postService.createPost(any())).thenReturn(createdPost);
        Mockito.when(postMapper.postEntityToPostDetails(any())).thenReturn(postDetails);

        String serializedCreatePost = objectMapper.writeValueAsString(createPost);

        MvcResult mvcResult = mockMvc
            .perform(
                post("/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreatePost)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PostDetails gotPost = objectMapper.readValue(jsonResponse, PostDetails.class);

        //then
        Mockito.verify(postMapper).createPostToPostEntity(createPost);
        Mockito.verify(postService).createPost(post);
        Mockito.verify(postMapper).postEntityToPostDetails(createdPost);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotCreatePostWithInvalidInput() throws Exception {

        //given
        CreatePost createPost = new CreatePost(
            "",
            null,
            true,
            false
        );

        //when
        String serializedCreatePost = objectMapper.writeValueAsString(createPost);

        MvcResult mvcResult = mockMvc
            .perform(
                post("/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreatePost)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        //then
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        FormExceptionResponse formExceptionResponse = objectMapper.readValue(jsonResponse, FormExceptionResponse.class);

        assertEquals(2, formExceptionResponse.errors().size());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotCreatePostWhenUserWasNotFound() throws Exception {

        //given
        CreatePost createPost = new CreatePost(
            "Opis postu",
            "Zawartość postu",
            true,
            false
        );

        PostEntity toCreatePost = new PostEntity();

        //when
        Mockito.when(postMapper.createPostToPostEntity(any())).thenReturn(toCreatePost);
        Mockito.when(postService.createPost(any())).thenThrow(EntityNotFoundException.class);

        String serializedCreatePost = objectMapper.writeValueAsString(createPost);

        mockMvc
            .perform(
                post("/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreatePost)
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(postService).createPost(toCreatePost);
    }

    @Test
    void shouldNotCreatePostWhenUserIsUnlogged() throws Exception {

        CreatePost createPost = new CreatePost(
            "Opis postu",
            "Zawartość postu",
            true,
            false
        );

        String serializedCreatePost = objectMapper.writeValueAsString(createPost);

        mockMvc
            .perform(
                post("/posts")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializedCreatePost)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldPatchPostById() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        PatchPost patchPost = new PatchPost(
            "Opis postu",
            true,
            false
        );

        PatchPostData patchPostData = new PatchPostData(
            "Opis postu",
            true,
            false
        );

        PostEntity changedPost = new PostEntity();

        PostDetails postDetails = new PostDetails(
            UUID.randomUUID().toString(),
            OffsetDateTime.now(),
            "Opis postu",
            "Zawartość postu",
            true,
            false,
            null,
            1,
            2
        );

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);
        Mockito.when(postMapper.patchPostToPatchPostData(any())).thenReturn(patchPostData);
        Mockito.when(postService.patchPostById(any(), any())).thenReturn(changedPost);
        Mockito.when(postMapper.postEntityToPostDetails(any())).thenReturn(postDetails);

        String serializedPatchPost = objectMapper.writeValueAsString(patchPost);

        MvcResult mvcResult = mockMvc
            .perform(
                patch(URL_PREFIX + postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedPatchPost)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        PostDetails gotPost = objectMapper.readValue(jsonResponse, PostDetails.class);

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(postMapper).patchPostToPatchPostData(patchPost);
        Mockito.verify(postService).patchPostById(postId, patchPostData);
        Mockito.verify(postMapper).postEntityToPostDetails(changedPost);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotPatchPostByIdWhenUserIsNotAnAuthor() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        PatchPost patchPost = new PatchPost(
            "Opis postu",
            true,
            false
        );

        PatchPostData patchPostData = new PatchPostData(
            "Opis postu",
            true,
            false
        );

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);
        Mockito.when(postMapper.patchPostToPatchPostData(any())).thenReturn(patchPostData);
        Mockito.when(postService.patchPostById(any(), any())).thenThrow(UserIsNotResourceAuthorException.class);

        String serializedPatchPost = objectMapper.writeValueAsString(patchPost);

        mockMvc
            .perform(
                patch(URL_PREFIX + postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedPatchPost)
            )
            .andDo(print())
            .andExpect(status().isForbidden());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(postMapper).patchPostToPatchPostData(patchPost);
        Mockito.verify(postService).patchPostById(postId, patchPostData);
    }

    @Test
    void shouldNotPatchPostByIdWhenUserUnlogged() throws Exception {

        UUID postId = UUID.randomUUID();

        PatchPost patchPost = new PatchPost(
            "Opis postu",
            true,
            false
        );

        String serializedPatchPost = objectMapper.writeValueAsString(patchPost);

        mockMvc
            .perform(
                patch(URL_PREFIX + postId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializedPatchPost)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldDeletePostById() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);

        mockMvc
            .perform(
                delete(URL_PREFIX + postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isNoContent());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(postService).deletePostById(postId);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotDeletePostByIdWhenUserIsNotAnAuthor() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(any(), anyString())).thenReturn(postId);
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(postService).deletePostById(any());

        mockMvc
            .perform(
                delete(URL_PREFIX + postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isForbidden());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(postService).deletePostById(postId);
    }

    @Test
    void shouldNotDeletePostByIdWhenUserIsUnlogged() throws Exception {

        UUID postId = UUID.randomUUID();

        mockMvc
            .perform(
                delete(URL_PREFIX + postId)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}