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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.PostMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.FollowersResponse;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.Followers;
import pl.instagram.instagram.model.domain.PostEntityForLoggedUser;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.FollowerService;
import pl.instagram.instagram.service.PostService;

import java.util.UUID;

@RestController
@CrossOrigin(value = "*")
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowerController {

    private final FollowerService followerService;
    private final PostService postService;

    private final UUIDMapper uuidMapper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    private static final String FOLLOWER_MESSAGE = "obserwującego";
    private static final String FOLLOWED_MESSAGE = "obserwowanego";

    @Operation(
        summary = "Get user's followers page by his id"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user's followers",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
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
    public ResponseEntity<Page<UserHeader>> getFollowersPage(@PathVariable("followedId") String followedIdStr, Pageable pageable){

        UUID followedId = uuidMapper.strToUUID(followedIdStr, FOLLOWED_MESSAGE);

        Page<UserEntity> followersPage = followerService.getFollowersPage(followedId, pageable);
        Page<UserHeader> followersHeadersPage = followersPage.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(followersHeadersPage);
    }

    @Operation(
        summary = "Get user's followed other users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found followed users page",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid follower id was given",
            content = @Content
        )
    })
    @GetMapping("/{followerId}/followed")
    public ResponseEntity<Page<UserHeader>> getFollowedPage(@PathVariable("followerId") String followerIdStr, Pageable pageable){

        UUID followerId = uuidMapper.strToUUID(followerIdStr, FOLLOWER_MESSAGE);

        Page<UserEntity> gotFollowedPage = followerService.getFollowedPage(followerId, pageable);
        Page<UserHeader> gotFollowedHeadersPage = gotFollowedPage.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(gotFollowedHeadersPage);
    }

    @Operation(summary = "Get posts page of users followed by logged user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found posts",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "No pagination was given",
            content = @Content
        )
    })
    @GetMapping("/followed/posts")
    public ResponseEntity<Page<PostDetails>> getPostsByFollowedUsers(Pageable pageable){

        Page<PostEntityForLoggedUser> gotPosts = postService.getPostsFromFollowedUsers(pageable);
        Page<PostDetails> convertedPosts = gotPosts.map(postMapper::postEntityForLoggedUserToPostDetails);

        return ResponseEntity.ok(convertedPosts);
    }

    @Operation(summary = "Create follow by logged user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create follow and get follower basic info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid followed user id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Followed or follower users were not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Logged user already followed given other user",
            content = @Content
        )
    })
    @PostMapping(value = "/{followedId}/followers")
    public ResponseEntity<UserHeader> createFollow(@PathVariable("followedId") String followedIdStr){

        UUID followedId = uuidMapper.strToUUID(followedIdStr, FOLLOWED_MESSAGE);

        UserEntity follower = followerService.createFollow(followedId);
        UserHeader followerHeader = userMapper.userEntityToUserHeader(follower);

        return ResponseEntity.status(HttpStatus.CREATED).body(followerHeader);
    }

    @Operation(summary = "Delete logged user follow")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Follow was removed",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid followed id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Followed user or follow were not found",
            content = @Content
        )
    })
    @DeleteMapping(value = "/{followedId}/followers")
    public ResponseEntity<Void> deleteFollow(@PathVariable("followedId") String followedIdStr){

        UUID followedId = uuidMapper.strToUUID(followedIdStr, FOLLOWED_MESSAGE);

        followerService.deleteFollow(followedId);

        return ResponseEntity.noContent().build();
    }
}
