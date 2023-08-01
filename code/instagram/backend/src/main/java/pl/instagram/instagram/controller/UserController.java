package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.BasicUserData;
import pl.instagram.instagram.model.api.response.UserProfileInfo;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.UserService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @GetMapping("/{id}/basic")
    ResponseEntity getUserById(@PathVariable("id") String userIdStr){

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

    @PostMapping("/{userAccountId}")
    ResponseEntity registerUser(@PathVariable("userAccountId") String userAccountIdStr){

        UUID userAccountId;

        try{
            userAccountId = UUID.fromString(userAccountIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        try{
            userService.createUser(userAccountId);
        }
        catch(ConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
