package pl.instagram.instagram.controller.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.instagram.instagram.config.SecurityConfig;
import pl.instagram.instagram.controller.CommentController;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.CommentEntityForLoggedUser;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.CommentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private UUIDMapper uuidMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String POST_MAPPER_MESSAGE = "postu";
    private static final String USER_MAPPER_MESSAGE = "użytkownika";
    private static final String COMMENT_MAPPER_MESSAGE = "komentarza";

    private static final String URL_PREFIX = "/posts/";
    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwSFlTYkVhVFpKa3NtN09TcDNYMEtsT2d1RmNETUhBWEBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzMwMjE0MjE4LCJleHAiOjE3MzAzMDA2MTgsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6InBIWVNiRWFUWkprc203T1NwM1gwS2xPZ3VGY0RNSEFYIn0.d1nIs3Yh3BPZ_Msxk9dRJx54AQj8cT8BVFZ8iWNh8EqA1M5h-PSZd_t9uT8rSWTMtjMDHELlNULj-Ns_HgFOS4J4_YeNJNGqb0y-Av9mCyEv27DmPYGR2dPIx9oHa2f4sCxWs9eNY9-iOV7FuMi0xyjqZYiJu5zGQnm3kMEDL_ztfUFBKwk6l3sL_l0KSLMY9vRltpPUgxj40EYAlrpJ0hEFeLpa27j2Hgs9Cc_NLqcPZnlNI5tFW9GSA8O3Uh1Azo-drjyLRoZ970035dDyNhIMy79xklLnV0yezpeptCGNZ21PVFmwtgQWD11prlMi1TQzskgjigUtLDo5Dr8sxg";

    @BeforeAll
    private static void setUp(){
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNull() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        OffsetDateTime c1OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime c2OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        CommentEntityForLoggedUser c1 = CommentEntityForLoggedUser.builder()
            .content("Dobre zdjecie")
            .creationDatetime(c1OffsetDateTime.toLocalDateTime())
            .build();

        c1.setId(UUID.randomUUID());

        CommentEntityForLoggedUser c2 = CommentEntityForLoggedUser.builder()
            .content("Zgadzam się")
            .creationDatetime(c2OffsetDateTime.toLocalDateTime())
            .build();

        c2.setId(UUID.randomUUID());

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "Avatar",
            true
        );

        CommentData commentData = new CommentData(
            c1.getId().toString(),
            userHeader,
            c1.getContent(),
            c1OffsetDateTime,
            1,
            2
        );

        Page<CommentEntityForLoggedUser> commentsPage = new PageImpl<>(
            List.of(c1, c2)
        );

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(postId);
        Mockito.when(commentService.getPostCommentsPage(any(), any(), any())).thenReturn(commentsPage);
        Mockito.when(commentMapper.commentEntityForLoggedUserToCommentData(any())).thenReturn(commentData);

        MvcResult mvcResult = mockMvc
            .perform(
                get(URL_PREFIX + postId + "/comments")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Page<CommentData> gotCommentsPage = objectMapper.readValue(jsonResponse, RestPage.class);
        List<CommentData> gotComments = objectMapper.convertValue(gotCommentsPage.getContent(), new TypeReference<List<CommentData>>(){});

        //then
        assertEquals(commentsPage.getTotalElements(), gotCommentsPage.getTotalElements());
        assertEquals(commentData, gotComments.get(0));

        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(commentService).getPostCommentsPage(postId, null, pageable);
        Mockito.verify(commentMapper).commentEntityForLoggedUserToCommentData(c1);
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNotNull() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();

        OffsetDateTime c1OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime c2OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        CommentEntityForLoggedUser c1 = CommentEntityForLoggedUser.builder()
            .content("Dobre zdjecie")
            .creationDatetime(c1OffsetDateTime.toLocalDateTime())
            .build();

        c1.setId(UUID.randomUUID());

        CommentEntityForLoggedUser c2 = CommentEntityForLoggedUser.builder()
            .content("Zgadzam się")
            .creationDatetime(c2OffsetDateTime.toLocalDateTime())
            .build();

        c2.setId(UUID.randomUUID());

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "Avatar",
            true
        );

        CommentData commentData = new CommentData(
            c1.getId().toString(),
            userHeader,
            c1.getContent(),
            c1OffsetDateTime,
            1,
            2
        );

        Page<CommentEntityForLoggedUser> commentsPage = new PageImpl<>(
            List.of(c1, c2)
        );

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(uuidMapper.strToUUID(eq(postId.toString()), anyString())).thenReturn(postId);
        Mockito.when(uuidMapper.strToUUID(eq(parentCommentId.toString()), anyString())).thenReturn(parentCommentId);
        Mockito.when(commentService.getPostCommentsPage(any(), any(), any())).thenReturn(commentsPage);
        Mockito.when(commentMapper.commentEntityForLoggedUserToCommentData(any())).thenReturn(commentData);

        MvcResult mvcResult = mockMvc
            .perform(
                get(URL_PREFIX + postId + "/comments")
                .param("parentCommentId", parentCommentId.toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Page<CommentData> gotCommentsPage = objectMapper.readValue(jsonResponse, RestPage.class);
        List<CommentData> gotComments = objectMapper.convertValue(gotCommentsPage.getContent(), new TypeReference<List<CommentData>>(){});

        //then
        assertEquals(commentsPage.getTotalElements(), gotCommentsPage.getTotalElements());
        assertEquals(commentData, gotComments.get(0));
        assertEquals(commentData, gotComments.get(1));

        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(uuidMapper).strToUUID(parentCommentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).getPostCommentsPage(postId, parentCommentId, pageable);
        Mockito.verify(commentMapper).commentEntityForLoggedUserToCommentData(c1);
    }

    @Test
    void shouldNotGetPostCommentsPageWhenInvalidPostIdWasGiven() throws Exception {

        //given
        String postIdStr = "A";

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenThrow(IllegalArgumentException.class);

        mockMvc
            .perform(
                get(URL_PREFIX + postIdStr + "/comments")
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
        Mockito.verify(uuidMapper).strToUUID(postIdStr, POST_MAPPER_MESSAGE);
    }

    @Test
    void shouldNotGetPostCommentsPageWhenPostWasNotFound() throws Exception {

        //given
        UUID postId = UUID.randomUUID();

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(postId);
        Mockito.when(commentService.getPostCommentsPage(any(), any(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc
            .perform(
                get(URL_PREFIX + postId + "/comments")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(commentService).getPostCommentsPage(postId, null, pageable);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldCreatePostCommentWhenParentCommentIdIsNull() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        String content = "Dobre zdjecie";

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "Avatar",
            true
        );

        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        CommentEntity comment = CommentEntity.builder()
            .author(new UserEntity())
            .content(content)
            .creationDatetime(offsetDateTime.toLocalDateTime())
            .subCommentsCount(1)
            .likesCount(2)
            .build();

        comment.setId(UUID.randomUUID());

        CommentData commentData = new CommentData(
            comment.getId().toString(),
            userHeader,
            comment.getContent(),
            offsetDateTime,
            comment.getSubCommentsCount(),
            comment.getLikesCount()
        );

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(postId);
        Mockito.when(commentService.createComment(any(), any(), any())).thenReturn(comment);
        Mockito.when(commentMapper.commentEntityToCommentData(any())).thenReturn(commentData);

        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        MvcResult mvcResult = mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CommentData createdCommentData = objectMapper.readValue(jsonResponse, CommentData.class);

        //then
        assertEquals(commentData, createdCommentData);

        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(commentService).createComment(postId, Optional.empty(), content);
        Mockito.verify(commentMapper).commentEntityToCommentData(comment);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldCreatePostCommentWhenParentCommentIdIsNotNull() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();
        String content = "Dobre zdjecie";

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "Avatar",
            true
        );

        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        CommentEntity comment = CommentEntity.builder()
            .author(new UserEntity())
            .content(content)
            .creationDatetime(offsetDateTime.toLocalDateTime())
            .subCommentsCount(1)
            .likesCount(2)
            .build();

        comment.setId(UUID.randomUUID());

        CommentData commentData = new CommentData(
            comment.getId().toString(),
            userHeader,
            comment.getContent(),
            offsetDateTime,
            comment.getSubCommentsCount(),
            comment.getLikesCount()
        );

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        Mockito.when(uuidMapper.strToUUID(eq(postId.toString()), anyString())).thenReturn(postId);
        Mockito.when(uuidMapper.strToUUID(eq(parentCommentId.toString()), anyString())).thenReturn(parentCommentId);
        Mockito.when(commentService.createComment(any(), any(), any())).thenReturn(comment);
        Mockito.when(commentMapper.commentEntityToCommentData(any())).thenReturn(commentData);

        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        MvcResult mvcResult = mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .param("parentCommentId", parentCommentId.toString())
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CommentData createdCommentData = objectMapper.readValue(jsonResponse, CommentData.class);

        //then
        assertEquals(commentData, createdCommentData);

        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(uuidMapper).strToUUID(parentCommentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).createComment(postId, Optional.of(parentCommentId), content);
        Mockito.verify(commentMapper).commentEntityToCommentData(comment);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotCreatePostCommentWhenPostWasNotFound() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        String content = "Dobre zdjęcie";

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(postId);
        Mockito.when(commentService.createComment(any(), any(), any())).thenThrow(EntityNotFoundException.class);

        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(uuidMapper).strToUUID(postId.toString(), POST_MAPPER_MESSAGE);
        Mockito.verify(commentService).createComment(postId, Optional.empty(), content);

    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotCreatePostCommentWhenGivenContentWasInvalid() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        String content = " ";

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotCreatePostCommentWhenGivenParentCommentWasNotFound() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();
        String content = "Dobre zdjęcie";

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        Mockito.when(commentService.createComment(any(), any(), any())).thenThrow(EntityNotFoundException.class);

        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .param("parentCommentId", parentCommentId.toString())
            )
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
    }

    @Test
    void shouldNotCreatePostCommentWhenUserIsUnlogged() throws Exception {

        //given
        UUID postId = UUID.randomUUID();
        String content = "Dobre zdjęcie";

        CreateComment createComment = new CreateComment(
            content
        );

        //when
        String serializedCreateComment = objectMapper.writeValueAsString(createComment);

        mockMvc
            .perform(
                post(URL_PREFIX + postId + "/comments")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedCreateComment)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());

        //then
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldUpdateComment() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = "Dobre zdjecie";

        UpdateComment updateComment = new UpdateComment(
            newContent
        );

        CommentEntity updatedComment = CommentEntity.builder()
            .content(newContent)
            .build();

        CommentData updatedCommentData = new CommentData(
            UUID.randomUUID().toString(),
            null,
            newContent,
            OffsetDateTime.now(),
            1,
            2
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(commentId);
        Mockito.when(commentService.updateComment(any(), anyString())).thenReturn(updatedComment);
        Mockito.when(commentMapper.commentEntityToCommentData(any())).thenReturn(updatedCommentData);

        String serializedUpdateComment = objectMapper.writeValueAsString(updateComment);

        MvcResult mvcResult = mockMvc
            .perform(
                put(URL_PREFIX + "comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedUpdateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CommentData gotCommentData = objectMapper.readValue(jsonResponse, CommentData.class);

        //then
        assertEquals(updatedCommentData.content(), gotCommentData.content());

        Mockito.verify(uuidMapper).strToUUID(commentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).updateComment(commentId, newContent);
        Mockito.verify(commentMapper).commentEntityToCommentData(updatedComment);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotUpdateCommentWhenInputIsBlank() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = " ";

        UpdateComment updateComment = new UpdateComment(
            newContent
        );

        //when
        String serializedUpdateComment = objectMapper.writeValueAsString(updateComment);

        mockMvc
            .perform(
                put(URL_PREFIX + "comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedUpdateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
    }

    @Test
    void shouldNotUpdateCommentWhenUserIsUnlogged() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = "Dobre zdjęcie";

        UpdateComment updateComment = new UpdateComment(
            newContent
        );

        //when
        String serializedUpdateComment = objectMapper.writeValueAsString(updateComment);

        mockMvc
            .perform(
                put(URL_PREFIX + "comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedUpdateComment)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotUpdateCommentWhenUserIsNotACommentAuthor() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = "Dobre zdjęcie";

        UpdateComment updateComment = new UpdateComment(
            newContent
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(commentId);
        Mockito.when(commentService.updateComment(any(), anyString())).thenThrow(UserIsNotResourceAuthorException.class);

        String serializedUpdateComment = objectMapper.writeValueAsString(updateComment);

        mockMvc
            .perform(
                put(URL_PREFIX + "comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializedUpdateComment)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isForbidden());

        //then
        Mockito.verify(uuidMapper).strToUUID(commentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).updateComment(commentId, newContent);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldRemoveComment() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(commentId);

        mockMvc
            .perform(
                delete(URL_PREFIX + "comments/" + commentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isNoContent());

        //then
        Mockito.verify(uuidMapper).strToUUID(commentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).deleteComment(commentId);
    }

    @Test
    void shouldNotRemoveCommentWhenUserIsUnlogged() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();

        //when
        mockMvc
            .perform(
                delete(URL_PREFIX + "comments/" + commentId)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldNotRemoveCommentWhenUserIsNotACommentAuthor() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(commentId);
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(commentService).deleteComment(any());

        mockMvc
            .perform(
                delete(URL_PREFIX + "comments/" + commentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            )
            .andDo(print())
            .andExpect(status().isForbidden());

        //then
        Mockito.verify(uuidMapper).strToUUID(commentId.toString(), COMMENT_MAPPER_MESSAGE);
        Mockito.verify(commentService).deleteComment(commentId);
    }
}