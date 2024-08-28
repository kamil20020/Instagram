package pl.instagram.instagram.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.FollowersResponse;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.Followers;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.FollowerService;

import java.util.UUID;

@RestController
@CrossOrigin(value = "*")
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowerController {

    private final FollowerService followerService;

    private final UUIDMapper uuidMapper;
    private final UserMapper userMapper;

    private static final String FOLLOWER_MESSAGE = "obserwującego";

    @Operation(
        summary = "Get user's followers page by his id",
        parameters = {
            @Parameter(name = "page", description = "Number of page of followers"),
            @Parameter(name = "size", description = "Size of the page")
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user's followers",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = FollowersResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid followed user id or no pagination were given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Followed user was not found",
            content = @Content
        )
    })
    @GetMapping(value = "/{followedId}/followers")
    public ResponseEntity<FollowersResponse> getFollowersPage(@PathVariable("followedId") String followedIdStr, Pageable pageable){

        UUID followedId = uuidMapper.strToUUID(followedIdStr, FOLLOWER_MESSAGE);

        Followers followers = followerService.getFollowersPage(followedId, pageable);
        Page<UserEntity> followersUsersPage = followers.followers();
        Page<UserHeader> followersUsersHeadersPage = followersUsersPage.map(userMapper::userEntityToUserHeader);

        FollowersResponse responseBody = new FollowersResponse(new RestPage<>(followersUsersHeadersPage), followers.didLoggedUserFollowed());

        return ResponseEntity.ok(responseBody);
    }
}
