package pl.instagram.instagram.controller.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.instagram.instagram.controller.UserController;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.domain.UserEntityForLoggedUser;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UUIDMapper uuidMapper;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String urlPrefix = "/users/";

    private static final String USER_MAPPER_MESSAGE = "użytkownika";

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImJFUnRKcG1leFhfcjJSVVNWMFZ4RSJ9.eyJpc3MiOiJodHRwczovL2Rldi0ybzJtbnhnMHBsY2xodGM3LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJCZkFLTm5ha0U5TXVBd3dVUlQwRXUyT1paNkY2ZDNqaUBjbGllbnRzIiwiYXVkIjoiaHR0cDovL2luc3RhZ3JhbS5jb20vIiwiaWF0IjoxNzI1MDQ3MTA1LCJleHAiOjE3MjUxMzM1MDUsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IkJmQUtObmFrRTlNdUF3d1VSVDBFdTJPWlo2RjZkM2ppIn0.BItftyrKWzpSVm15EN3L5QQisXnQV2_5enhJ8dNnAg3HLvotfnMQX3kXnJE7XHxSFML3YEAqf1tWcT4qPikmgC61NcHdSIRkOOk10jixl9XkSQi7-4C5Vv6alOxaMuzKjx0_MChnd6PGdfm8jv9RC1niQXqjfuZxahfhFg6x-hjC1oeLAhmK6T--g4IZkOzuGTlYl940jnwbGXDzwZSeRQfwOeL-fEixVwlbFPuv5iuUSvOAFcdTNh8o_ziKTPE1pTvQa8fHkh3Oew_nnlsK0aQmI1GGARAiGOi_k30cH1jHJlZQwiM69BHNvSRhRJbp4JU5Mq858z_lMbIi1TuZ7w";

    @Test
    void shouldGetUserHeaderById() throws Exception {

        //given
        UUID userId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        UserHeader userHeader = new UserHeader(
            userId.toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(userId);
        Mockito.when(userService.getUserById(any())).thenReturn(user);

        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        MvcResult mvcResult = mockMvc
            .perform(
                get(urlPrefix + userId + "/header")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Object objectResponse = objectMapper.readValue(jsonResponse, UserHeader.class);

        Mockito.verify(uuidMapper).strToUUID(userId.toString(), USER_MAPPER_MESSAGE);
        Mockito.verify(userService).getUserById(userId);
        Mockito.verify(userMapper).userEntityToUserHeader(any(UserEntity.class));
    }

    @Test
    void shouldNotGetUserHeaderByIdWhenUserIdIsInvalid() throws Exception {

        //given
        String userId = "A";

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenThrow(IllegalArgumentException.class);

        mockMvc
            .perform(get(urlPrefix + userId + "/header"))
            .andDo(print())
            .andExpect(status().isBadRequest());

        //then
        Mockito.verify(uuidMapper).strToUUID(userId, USER_MAPPER_MESSAGE);
    }

    @Test
    void shouldNotGetUserHeaderByIdWhenUserIdIsNotFound() throws Exception {

        //given
        UUID userId = UUID.randomUUID();

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(userId);
        Mockito.when(userService.getUserById(any())).thenThrow(EntityNotFoundException.class);

        mockMvc
            .perform(get(urlPrefix + userId + "/header"))
            .andDo(print())
            .andExpect(status().isNotFound());

        //then
        Mockito.verify(uuidMapper).strToUUID(userId.toString(), USER_MAPPER_MESSAGE);
        Mockito.verify(userService).getUserById(userId);
    }

    @Test
    void shouldGetUserProfileByUserId() throws Exception {

        //given
        UUID userId = UUID.randomUUID();

        UserEntityForLoggedUser user = new UserEntityForLoggedUser();
        UserProfile userProfile = new UserProfile(
            userId.toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false,
            false,
            "Opis",
            0,
            0,
            0
        );

        //when
        Mockito.when(uuidMapper.strToUUID(anyString(), anyString())).thenReturn(userId);
        Mockito.when(userService.getUserByIdForLoggedUser(any())).thenReturn(user);
        Mockito.when(userMapper.userEntityForLoggedToUserProfile(any())).thenReturn(userProfile);

        //then
        MvcResult mvcResult = mockMvc
            .perform(get(urlPrefix + userId + "/profile"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Object objectResponse = objectMapper.readValue(jsonResponse, UserProfile.class);

        Mockito.verify(uuidMapper).strToUUID(userId.toString(), USER_MAPPER_MESSAGE);
        Mockito.verify(userService).getUserByIdForLoggedUser(userId);
        Mockito.verify(userMapper).userEntityForLoggedToUserProfile(user);
    }

    @Test
    void shouldGetUserHeaderByUserAccountId() throws Exception {

        //given
        String accountId = "A";

        UserEntity user = new UserEntity();
        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false
        );

        //when
        Mockito.when(userService.getUserByUserAccountId(anyString())).thenReturn(user);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        //then
        MvcResult mvcResult = mockMvc
            .perform(
                get(urlPrefix + "user-account/" + accountId + "/header")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        Object objectResponse = objectMapper.readValue(jsonResponse, UserHeader.class);

        Mockito.verify(userService).getUserByUserAccountId(accountId);
        Mockito.verify(userMapper).userEntityToUserHeader(user);

    }

    @Test
    void shouldGetUsersByIds() throws Exception {

        //given
        ArrayList<UUID> ids = new ArrayList<>(List.of(UUID.randomUUID(), UUID.randomUUID()));
        List<String> idsStrs = ids.stream()
            .map(UUID::toString)
            .toList();

        List<UserEntity> users = List.of(new UserEntity(), new UserEntity());
        List<UserHeader> usersHeaders = List.of(
            new UserHeader(
                idsStrs.get(0),
                "kamil",
                "Kamil",
                "Kowalski",
                "avatar",
                false
            ),
            new UserHeader(
                idsStrs.get(1),
                "kamil",
                "Kamil",
                "Kowalski",
                "avatar",
                false
            )
        );

        //when
        Mockito.when(uuidMapper.strListToUUIDList(anyList(), anyString())).thenReturn(ids);
        Mockito.when(userService.getUsersByIds(anyList())).thenReturn(users);
        Mockito.when(userMapper.userEntityListToUserHeaderList(anyList())).thenReturn(usersHeaders);

        //then
        MvcResult mvcResult = mockMvc
            .perform(
                get(urlPrefix + "ids")
                .param("ids", idsStrs.toArray(new String[0]))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserHeader[] objectResponse = objectMapper.readValue(jsonResponse, UserHeader[].class);

        assertThat(objectResponse.length).isEqualTo(2);

        Mockito.verify(uuidMapper).strListToUUIDList(idsStrs, USER_MAPPER_MESSAGE);
        Mockito.verify(userService).getUsersByIds(ids);
        Mockito.verify(userMapper).userEntityListToUserHeaderList(users);
    }

    @Test
    void shouldSearchUsers() throws Exception {

        //given
        String phrase = "kamil kowalski";

        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);

        UserEntity user = new UserEntity();

        Page<UserEntity> usersPage = new PageImpl<>(
            List.of(user)
        );

        UserHeader userHeader = new UserHeader(
            UUID.randomUUID().toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false
        );

        //when
        Mockito.when(userService.searchUsers(anyString(), any())).thenReturn(usersPage);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        //then
        MvcResult mvcResult = mockMvc
            .perform(
                get("/users")
                .param("phrase", phrase)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertTrue(jsonResponse.contains("totalPages"));
        assertTrue(jsonResponse.contains("totalElements"));
        assertTrue(jsonResponse.contains("size"));
        assertTrue(jsonResponse.contains("content"));

        Mockito.verify(userService).searchUsers(phrase, pageable);
        Mockito.verify(userMapper).userEntityToUserHeader(user);
    }

    @Test
    void shouldRegisterUser() throws Exception {

        //given
        String accountId = "A";
        UUID userId = UUID.randomUUID();

        //when
        Mockito.when(userService.createUser(anyString())).thenReturn(userId);
        Mockito.when(uuidMapper.uuidToStr(any())).thenReturn(userId.toString());

        //then
        mockMvc
            .perform(
                post(urlPrefix + "register")
                .with(csrf())
                .param("accountId", accountId)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().string(userId.toString()));

        Mockito.verify(userService).createUser(accountId);
        Mockito.verify(uuidMapper).uuidToStr(userId);
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldFillPersonalData() throws Exception {

        //given
        UUID userId = UUID.randomUUID();

        PersonalData personalData = new PersonalData(
            "kamil",
            "Kamil",
            "Kowalski"
        );

        UserEntity convertedPersonalData = new UserEntity();

        convertedPersonalData.setNickname(personalData.nickname());
        convertedPersonalData.setFirstname(personalData.firstname());
        convertedPersonalData.setSurname(personalData.surname());

        UserEntity changedUser = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .creationDatetime(LocalDateTime.now())
            .build();

        changedUser.setId(userId);

        UserHeader userHeader = new UserHeader(
            userId.toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false
        );

        //when
        Mockito.when(userMapper.userPersonalDataToUserEntity(any())).thenReturn(convertedPersonalData);
        Mockito.when(userService.fillPersonalData(any())).thenReturn(changedUser);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        //then
        String jsonRequestBody = objectMapper.writeValueAsString(personalData);

        MvcResult mvcResult = mockMvc
            .perform(
                post(urlPrefix + "fill-personal-data")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonRequestBody)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        UserHeader userResponse = objectMapper.readValue(jsonResponse, UserHeader.class);

        Mockito.verify(userMapper).userPersonalDataToUserEntity(personalData);
        Mockito.verify(userService).fillPersonalData(convertedPersonalData);
        Mockito.verify(userMapper).userEntityToUserHeader(changedUser);
    }

    @Test
    void shouldNotFillPersonalDataWhenUnlogged() throws Exception {

        PersonalData personalData = new PersonalData(
            "kamil",
            "Kamil",
            "Kowalski"
        );

        String serializedPersonalData = objectMapper.writeValueAsString(personalData);

        MvcResult mvcResult = mockMvc
            .perform(
                post(urlPrefix + "fill-personal-data")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializedPersonalData)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn();
    }

    @Test
    @WithMockUser(username = "BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
    void shouldPatchLoggedUser() throws Exception {

        //given
        UUID userId = UUID.randomUUID();

        UpdateUser updateUser = new UpdateUser(
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar"
        );

        UserEntity convertedUpdateUser = new UserEntity();

        convertedUpdateUser.setNickname(updateUser.nickname());
        convertedUpdateUser.setFirstname(updateUser.nickname());
        convertedUpdateUser.setSurname(updateUser.nickname());
        convertedUpdateUser.setAvatar(("avatar").getBytes(StandardCharsets.UTF_8));

        UserEntity changedUser = UserEntity.builder()
            .accountId("BfAKNnakE9MuAwwURT0Eu2OZZ6F6d3ji")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .avatar(("avatar").getBytes(StandardCharsets.UTF_8))
            .creationDatetime(LocalDateTime.now())
            .build();

        changedUser.setId(userId);

        UserHeader userHeader = new UserHeader(
            userId.toString(),
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar",
            false
        );

        //when
        Mockito.when(userMapper.updateUserToUserEntity(any())).thenReturn(convertedUpdateUser);
        Mockito.when(userService.patchUser(any())).thenReturn(changedUser);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        //then
        String requestBodyStr = objectMapper.writeValueAsString(updateUser);

        MvcResult mvcResult = mockMvc
            .perform(
                patch("/users")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBodyStr)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponseStr = mvcResult.getResponse().getContentAsString();
        UserHeader userResponse = objectMapper.readValue(jsonResponseStr, UserHeader.class);

        Mockito.verify(userMapper).updateUserToUserEntity(updateUser);
        Mockito.verify(userService).patchUser(convertedUpdateUser);
        Mockito.verify(userMapper).userEntityToUserHeader(changedUser);
    }

    @Test
    void shouldNotPatchLoggedUserWhenUnlogged() throws Exception {

        UpdateUser updateUser = new UpdateUser(
            "kamil",
            "Kamil",
            "Kowalski",
            "avatar"
        );

        String serializedUpdateUser = objectMapper.writeValueAsString(updateUser);

        MvcResult mvcResult = mockMvc
            .perform(
                patch("/users")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(serializedUpdateUser)
            )
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn();
    }
}