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
    private final PostService postService;

    private final UserMapper userMapper;
    private final ByteArrayMapper byteArrayMapper;
    private final UUIDMapper uuidMapper;

    @GetMapping("/{id}")
    ResponseEntity<UserHeader> getUserHeaderById(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, "użytkownika");

        UserEntity foundUser = userService.getUserById(userId);
        UserHeader foundUserHeader = userMapper.userEntityToBasicUserData(foundUser);

        return ResponseEntity.ok(foundUserHeader);
    }

    @GetMapping("/{id}/profile")
    ResponseEntity<UserProfile> getUserProfile(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, "użytkownika");

        UserEntity foundUser = userService.getUserById(userId);
        UserProfile foundUserProfile = userMapper.userEntityToUserProfileInfo(foundUser);

        return ResponseEntity.ok(foundUserProfile);
    }


    @GetMapping("/{id}/posts")
    ResponseEntity<Page<PostHeader>> getUserPostsHeadersPage(@PathVariable("id") String userIdStr, Pageable pageable){

        UUID userId = uuidMapper.strToUUID(userIdStr, "użytkownika");

        Page<PostEntity> foundPostsPage = postService.getUserPostsPage(userId, pageable);

        Page<PostHeader> foundPostsHeaders = foundPostsPage.map(post -> PostHeader.builder()
            .id(post.getId().toString())
            .img(byteArrayMapper.byteArrayToBase64(post.getContent()))
            .build()
        );

        return ResponseEntity.ok(foundPostsHeaders);
    }

    @GetMapping("/user-account/{userAccountId}")
    ResponseEntity<UserHeader> getUserHeaderByUserAccountId(@PathVariable("userAccountId") String userAccountId){

        UserEntity foundUser = userService.getUserByUserAccountId(userAccountId);
        UserHeader userHeader = userMapper.userEntityToBasicUserData(foundUser);

        return ResponseEntity.ok(userHeader);
    }

    @GetMapping("/ids")
    ResponseEntity<List<UserHeader>> getUsersByIds(@RequestParam(name = "ids") String[] idsArr) {

        List<String> idsList = Arrays.asList(idsArr);
        List<UUID> ids = uuidMapper.strListToUUIDList(idsList, "użytkownika");

        List<UserEntity> foundUsers = userService.getUsersByIds(ids);
        List<UserHeader> foundUsersHeaders = userMapper.userEntityListToBasicUserDataList(foundUsers);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @GetMapping
    ResponseEntity<Page<UserHeader>> searchUser(@RequestParam(name = "phrase") String phrase, Pageable pageable){

        Page<UserEntity> foundUsers = userService.searchUsers(phrase, pageable);
        Page<UserHeader> foundUsersHeaders = foundUsers.map(userMapper::userEntityToBasicUserData);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestParam String accountId){

        UUID createdUserId = userService.createUser(accountId);
        String createdUserIdStr = uuidMapper.uuidToStr(createdUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserIdStr);
    }

    @PatchMapping("/basic")
    ResponseEntity<UserHeader> patchLoggedUserBasic(Principal principal, @RequestBody UpdateUser updateRequest){

        String userAccountId = principal.getName();

        UserEntity updateUser = userMapper.updateUserToUserEntity(updateRequest);

        UserEntity patchedUser = userService.patchUser(userAccountId, updateUser);
        UserHeader patchedUserHeader = userMapper.userEntityToBasicUserData(patchedUser);

        return ResponseEntity.ok(patchedUserHeader);
    }
}
