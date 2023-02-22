package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.BasicUserData;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.UserService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @GetMapping(value = "/{id}")
    ResponseEntity getUserById(@PathVariable("id") String userIdStr){

        Integer userId;

        try{
            userId = Integer.valueOf(userIdStr);
        }
        catch(NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        UserEntity foundUser = userService.getUserById(userId);
        BasicUserData foundUserBasicData = userMapper.userEntityToBasicUserData(foundUser);

        return ResponseEntity.ok(foundUserBasicData);
    }

    @GetMapping(value = "/all")
    ResponseEntity<List<UserEntity>> getAllUsers(){

        List<UserEntity> foundUsers = userService.getAllUsers();

        return ResponseEntity.ok(foundUsers);
    }

    @GetMapping(value = "/ids")
    ResponseEntity getUsersByIds(@RequestParam(name = "ids") String[] idsStrs) throws UnsupportedEncodingException {

        List<Integer> ids;

        try{
            ids = Arrays.stream(idsStrs)
                .map(idStr -> Integer.valueOf(idStr))
                .collect(Collectors.toList());
        }
        catch(NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    @GetMapping(value = "/search")
    ResponseEntity<List<BasicUserData>> searchUser(@RequestParam(name = "input") String input){

        List<UserEntity> foundUsers = userService.searchUsers(input);
        List<BasicUserData> foundUsersBasicData = userMapper.userEntityListToBasicUserDataList(foundUsers);

        return ResponseEntity.ok(foundUsersBasicData);
    }
}
