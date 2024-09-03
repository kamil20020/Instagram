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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.PostLikesResponse;
import pl.instagram.instagram.model.api.response.RestPage;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.PostLikes;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.PostLikeService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    private final UserMapper userMapper;
    private final UUIDMapper uuidMapper;

    private static final String POST_MAPPER_MESSAGE = "post";

    @Operation(
        summary = "Get post likes by its id",
        parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found post likes",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PostLikesResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Got invalid post id or no pagination settings were given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post was not found",
            content = @Content
        )
    })
    @GetMapping("/{id}/likes")
    ResponseEntity<PostLikesResponse> getPostLikesPage(@PathVariable("id") String postIdStr, Pageable pageable) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostLikes foundPostLikesDetails = postLikeService.getPostLikes(postId, pageable);
        Page<UserHeader> foundPostLikesHeadersPage = foundPostLikesDetails.postLikes()
            .map(userMapper::userEntityToUserHeader);
        PostLikesResponse foundPostsLikesResponse = new PostLikesResponse(
            postId.toString(), new RestPage<>(foundPostLikesHeadersPage), foundPostLikesDetails.didLoggedUserLikePost()
        );
        return ResponseEntity.ok(foundPostsLikesResponse);
    }

    @Operation(
        summary = "Create post like by logged user",
        parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Post like was created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "Post like author's basic data", implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid post id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post id or logged user id were not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Logged user is already an author of given post like"
        )
    })
    @PostMapping("/{id}/likes")
    ResponseEntity<UserHeader> createPostLike(@PathVariable("id") String postIdStr){

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        UserEntity likeAuthor = postLikeService.addLike(postId);
        UserHeader likeAuthorHeader = userMapper.userEntityToUserHeader(likeAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeAuthorHeader);
    }

    @Operation(
            summary = "Delete post like by post id by logged user",
            parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Post like was deleted",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid post id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post id or logged user id were not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}/likes")
    ResponseEntity<Void> deletePostLike(@PathVariable("id") String postIdStr){

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        postLikeService.removeLike(postId);

        return ResponseEntity.noContent().build();
    }
}
