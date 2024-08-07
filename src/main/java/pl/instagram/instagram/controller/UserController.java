package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.ByteArrayMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.BasicUserData;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.UserProfileInfo;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.PostService;
import pl.instagram.instagram.service.UserService;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final UserMapper userMapper;
    private final ByteArrayMapper byteArrayMapper;

    @GetMapping("/{id}/basic")
    ResponseEntity getBasicUserById(@PathVariable("id") String userIdStr){

        UUID userId;

        try{
            userId = UUID.fromString(userIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        UserEntity foundUser = userService.getUserById(userId);
        BasicUserData foundUserBasicData = userMapper.userEntityToBasicUserData(foundUser);

        return ResponseEntity.ok(foundUserBasicData);
    }

    @GetMapping("/{id}/profile")
    ResponseEntity getUserProfileInfo(@PathVariable("id") String userIdStr){

        UUID userId;

        try{
            userId = UUID.fromString(userIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        UserEntity foundUser = userService.getUserById(userId);
        UserProfileInfo foundUserProfileInfo = userMapper.userEntityToUserProfileInfo(foundUser);

        return ResponseEntity.ok(foundUserProfileInfo);
    }


    @GetMapping("/{id}/posts")
    ResponseEntity getUserPostsHeadersPage(@PathVariable("id") String userIdStr, Pageable pageable){

        if(pageable == null){
            pageable = Pageable.unpaged();
        }

        UUID userId;

        try{
            userId = UUID.fromString(userIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        Page<PostEntity> foundPostsPage = postService.getUserPostsPage(userId, pageable);

        Page<PostHeader> foundPostsHeaders = foundPostsPage.map(post -> PostHeader.builder()
            .id(post.getId().toString())
            .img(byteArrayMapper.byteArrayToBase64(post.getContent()))
            .build()
        );

        return ResponseEntity.ok(foundPostsHeaders);
    }

    @GetMapping("/user-account/{userAccountId}")
    ResponseEntity getBasicUserByUserAccountId(@PathVariable("userAccountId") String userAccountId){

        UserEntity foundUser;

        try{
            foundUser = userService.getUserByUserAccountId(userAccountId);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        BasicUserData basicUserData = userMapper.userEntityToBasicUserData(foundUser);

        return ResponseEntity.ok(basicUserData);
    }

    @GetMapping("/all")
    ResponseEntity<List<UserEntity>> getAllUsers(){

        List<UserEntity> foundUsers = userService.getAllUsers();

        return ResponseEntity.ok(foundUsers);
    }

    @GetMapping("/ids")
    ResponseEntity getUsersByIds(@RequestParam(name = "ids") String[] idsStrs) {

        List<UUID> ids;

        try{
            ids = Arrays.stream(idsStrs)
                .map(idStr -> UUID.fromString(idStr))
                .collect(Collectors.toList());
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        List<UserEntity> foundUsers;

        try{
            foundUsers = userService.getUsersByIds(ids);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        List<BasicUserData> foundUsersBasicData = userMapper.userEntityListToBasicUserDataList(foundUsers);

        return ResponseEntity.ok(foundUsersBasicData);
    }

    @GetMapping
    ResponseEntity<List<BasicUserData>> searchUser(@RequestParam(name = "phrase") String phrase){

        List<UserEntity> foundUsers = userService.searchUsers(phrase);
        List<BasicUserData> foundUsersBasicData = userMapper.userEntityListToBasicUserDataList(foundUsers);

        return ResponseEntity.ok(foundUsersBasicData);
    }

    @PostMapping("/user-account/{userAccountId}")
    ResponseEntity registerUser(@PathVariable("userAccountId") String userAccountId){

        UUID createdUserId;

        try{
            createdUserId = userService.createUser(userAccountId);
        }
        catch(ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserId.toString());
    }

    @PatchMapping("/basic")
    ResponseEntity patchLoggedUserBasic(Principal principal, @RequestBody UpdateUser updateUser){

        String userAccountId = principal.getName();

        UserEntity patchedUser;

        try{
            patchedUser = userService.patchUser(userAccountId, userMapper.updateUserToUserEntity(updateUser));
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        BasicUserData patchedUserBasicData = userMapper.userEntityToBasicUserData(patchedUser);

        return ResponseEntity.ok(patchedUserBasicData);
    }
}
