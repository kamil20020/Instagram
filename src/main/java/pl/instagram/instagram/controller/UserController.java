package pl.instagram.instagram.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.ByteArrayMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.PostService;
import pl.instagram.instagram.service.UserService;

import java.security.Principal;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;
    private final UUIDMapper uuidMapper;

    private static final String USER_MAPPER_MESSAGE = "użytkownika";

    @GetMapping("/{id}/header")
    ResponseEntity<UserHeader> getUserHeaderById(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        UserEntity foundUser = userService.getUserById(userId);
        UserHeader foundUserHeader = userMapper.userEntityToUserHeader(foundUser);

        return ResponseEntity.ok(foundUserHeader);
    }

    @GetMapping("/{id}/profile")
    ResponseEntity<UserProfile> getUserProfile(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        UserEntity foundUser = userService.getUserById(userId);
        UserProfile foundUserProfile = userMapper.userEntityToUserProfileInfo(foundUser);

        return ResponseEntity.ok(foundUserProfile);
    }

    @GetMapping("/user-account/{userAccountId}/header")
    ResponseEntity<UserHeader> getUserHeaderByUserAccountId(@PathVariable("userAccountId") String userAccountId){

        UserEntity foundUser = userService.getUserByUserAccountId(userAccountId);
        UserHeader userHeader = userMapper.userEntityToUserHeader(foundUser);

        return ResponseEntity.ok(userHeader);
    }

    @GetMapping("/ids")
    ResponseEntity<List<UserHeader>> getUsersByIds(@RequestParam(name = "ids") String[] idsArr) {

        List<String> idsList = Arrays.asList(idsArr);
        List<UUID> ids = uuidMapper.strListToUUIDList(idsList, USER_MAPPER_MESSAGE);

        List<UserEntity> foundUsers = userService.getUsersByIds(ids);
        List<UserHeader> foundUsersHeaders = userMapper.userEntityListToUserHeaderList(foundUsers);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @GetMapping
    ResponseEntity<Page<UserHeader>> searchUsers(@RequestParam(name = "phrase") String phrase, Pageable pageable){

        Page<UserEntity> foundUsers = userService.searchUsers(phrase, pageable);
        Page<UserHeader> foundUsersHeaders = foundUsers.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestParam String accountId){

        UUID createdUserId = userService.createUser(accountId);
        String createdUserIdStr = uuidMapper.uuidToStr(createdUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserIdStr);
    }

    @PostMapping("/fill-personal-data")
    ResponseEntity<UserHeader> fillPersonalData(@Valid @RequestBody PersonalData personalData){

        UserEntity userPersonalData = userMapper.userPersonalDataToUserEntity(personalData);

        UserEntity updatedUser = userService.fillPersonalData(userPersonalData);
        UserHeader updatedUserHeader = userMapper.userEntityToUserHeader(updatedUser);

        return ResponseEntity.ok(updatedUserHeader);
    }

    @PatchMapping
    ResponseEntity<UserHeader> patchLoggedUser(@RequestBody UpdateUser updateRequest){

        UserEntity updateUser = userMapper.updateUserToUserEntity(updateRequest);

        UserEntity patchedUser = userService.patchUser(updateUser);
        UserHeader patchedUserHeader = userMapper.userEntityToUserHeader(patchedUser);

        return ResponseEntity.ok(patchedUserHeader);
    }
}
