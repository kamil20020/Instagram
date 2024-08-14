package pl.instagram.instagram.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.UserService;

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

    @Operation(
        summary = "Get user's basic info by his id",
        parameters = @Parameter(name = "id", description = "User's id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user's basic info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User was not found",
            content = @Content
        )
    })
    @GetMapping("/{id}/header")
    ResponseEntity<UserHeader> getUserHeaderById(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        UserEntity foundUser = userService.getUserById(userId);
        UserHeader foundUserHeader = userMapper.userEntityToUserHeader(foundUser);

        return ResponseEntity.ok(foundUserHeader);
    }

    @Operation(
        summary = "Get user's profile info by his id",
        parameters = @Parameter(name = "id", description = "User's id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user's profile info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserProfile.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User was not found",
            content = @Content
        )
    })
    @GetMapping("/{id}/profile")
    ResponseEntity<UserProfile> getUserProfile(@PathVariable("id") String userIdStr){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        UserEntity foundUser = userService.getUserById(userId);
        UserProfile foundUserProfile = userMapper.userEntityToUserProfileInfo(foundUser);

        return ResponseEntity.ok(foundUserProfile);
    }

    @Operation(
        summary = "Get user basic info by his user account id",
        parameters = @Parameter(name = "userAccountId", description = "User's account id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user basic info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User was not found",
            content = @Content
        )
    })
    @GetMapping("/user-account/{userAccountId}/header")
    ResponseEntity<UserHeader> getUserHeaderByUserAccountId(@PathVariable("userAccountId") String userAccountId){

        UserEntity foundUser = userService.getUserByUserAccountId(userAccountId);
        UserHeader userHeader = userMapper.userEntityToUserHeader(foundUser);

        return ResponseEntity.ok(userHeader);
    }

    @Operation(
        summary = "Get users basic infos by their ids",
        parameters = @Parameter(name = "ids", description = "Users' ids")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found users basic infos",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = UserHeader.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid ids were given",
            content = @Content
        ),
    })
    @GetMapping("/ids")
    ResponseEntity<List<UserHeader>> getUsersByIds(@RequestParam(name = "ids") String[] idsArr) {

        List<String> idsList = Arrays.asList(idsArr);
        List<UUID> ids = uuidMapper.strListToUUIDList(idsList, USER_MAPPER_MESSAGE);

        List<UserEntity> foundUsers = userService.getUsersByIds(ids);
        List<UserHeader> foundUsersHeaders = userMapper.userEntityListToUserHeaderList(foundUsers);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @Operation(
        summary = "Search users by phrase",
        parameters = {
            @Parameter(name = "phrase", description = "Search phrase")
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found users' basic data page",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Pagination is required",
            content = @Content
        )
    })
    @GetMapping
    ResponseEntity<Page<UserHeader>> searchUsers(@RequestParam(name = "phrase") String phrase, Pageable pageable){

        Page<UserEntity> foundUsers = userService.searchUsers(phrase, pageable);
        Page<UserHeader> foundUsersHeaders = foundUsers.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(foundUsersHeaders);
    }

    @Operation(
        summary = "Register user",
        parameters = @Parameter(name = "accountId", description = "User's id of account created in external auth provider")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User was registered",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "Created user's id", implementation = String.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with given nickname already exists",
            content = @Content()
        ),
    })
    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestParam(name = "accountId") String accountId){

        UUID createdUserId = userService.createUser(accountId);
        String createdUserIdStr = uuidMapper.uuidToStr(createdUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserIdStr);
    }

    @Operation(
        summary = "Fill logged user's personal data",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User's personal data")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User's data were saved",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "User's basic data", implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User does not exist",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with given nickname already exists",
            content = @Content()
        ),
    })
    @PostMapping("/fill-personal-data")
    ResponseEntity<UserHeader> fillPersonalData(@Valid @RequestBody PersonalData personalData){

        UserEntity userPersonalData = userMapper.userPersonalDataToUserEntity(personalData);

        UserEntity updatedUser = userService.fillPersonalData(userPersonalData);
        UserHeader updatedUserHeader = userMapper.userEntityToUserHeader(updatedUser);

        return ResponseEntity.ok(updatedUserHeader);
    }

    @Operation(
        summary = "Patch user's data",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User's new data")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User's data were changed",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "User's basic data", implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User does not exist",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with given nickname already exists",
            content = @Content()
        ),
    })
    @PatchMapping
    ResponseEntity<UserHeader> patchLoggedUser(@RequestBody UpdateUser updateRequest){

        UserEntity updateUser = userMapper.updateUserToUserEntity(updateRequest);

        UserEntity patchedUser = userService.patchUser(updateUser);
        UserHeader patchedUserHeader = userMapper.userEntityToUserHeader(patchedUser);

        return ResponseEntity.ok(patchedUserHeader);
    }
}
