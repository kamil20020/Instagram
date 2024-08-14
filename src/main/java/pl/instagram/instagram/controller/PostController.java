package pl.instagram.instagram.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.*;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.response.*;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.service.PostService;

import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UUIDMapper uuidMapper;
    private final PostMapper postMapper;

    private static final String POST_MAPPER_MESSAGE = "posta";
    private static final String USER_MAPPER_MESSAGE = "użytkownika";

    @Operation(
        summary = "Get post details by its id",
        parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found post details",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PostDetails.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post was not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    ResponseEntity<PostDetails> getPostById(@PathVariable("id") String postIdStr) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostEntity foundPost = postService.getPostById(postId);
        PostDetails postDetails = postMapper.postEntityToPostDetails(foundPost);

        return ResponseEntity.ok(postDetails);
    }

    @Operation(
        summary = "Get user's posts basic infos page by user's id",
        parameters = {
            @Parameter(name = "id", description = "User id")
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found user's posts basic info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id was given or pagination settings were not included in request",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User was not found",
            content = @Content
        )
    })
    @GetMapping("/{id}/posts")
    ResponseEntity<Page<PostHeader>> getUserPostsHeadersPage(@PathVariable("id") String userIdStr, Pageable pageable){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        Page<PostEntity> foundPostsPage = postService.getUserPostsPage(userId, pageable);
        Page<PostHeader> foundPostsHeaders = foundPostsPage.map(postMapper::postEntityToPostHeader);

        return ResponseEntity.ok(foundPostsHeaders);
    }

    @Operation(
        summary = "Create post",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New post data")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Post was created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "Created post details", implementation = PostDetails.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Author was not found",
            content = @Content
        )
    })
    @PostMapping
    ResponseEntity<PostDetails> createPost(@Valid @RequestBody CreatePost createPost){

        PostEntity toCreatePost = postMapper.createPostToPostEntity(createPost);

        PostEntity createdPost = postService.createPost(toCreatePost);
        PostDetails postDetails = postMapper.postEntityToPostDetails(createdPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDetails);
    }

    @Operation(
        summary = "Patch post by its id",
        parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Post data were patched",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(
                    description = "Patched post details", implementation = PostDetails.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Logged user is not an author of given post",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post was not found",
            content = @Content
        ),
    })
    @PatchMapping("/{id}")
    ResponseEntity<PostDetails> patchPostById(@PathVariable("id") String postIdStr, @RequestBody PostEntity postEntity) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostEntity changedPost = postService.patchPostById(postId, postEntity);
        PostDetails changedPostDetails = postMapper.postEntityToPostDetails(changedPost);

        return ResponseEntity.ok(changedPostDetails);
    }

    @Operation(
        summary = "Delete post by its id",
        parameters = @Parameter(name = "id", description = "Post id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Post was deleted",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Given post id is invalid",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Logged user is not an author of given post",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post was not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePostById(@PathVariable("id") String postIdStr){

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        postService.deletePostById(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}