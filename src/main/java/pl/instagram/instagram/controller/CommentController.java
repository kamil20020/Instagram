package pl.instagram.instagram.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jdk.jfr.MemoryAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.service.CommentService;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {

    private final CommentService commentService;

    private final CommentMapper commentMapper;
    private final UUIDMapper uuidMapper;

    private static final String COMMENT_MAPPER_MESSAGE = "komentarza";
    private static final String POST_MAPPER_MESSAGE = "postu";

    @Operation(
        summary = "Get post comments page in one level (e.g. root comments without their parent comments) by post id",
        parameters = {
            @Parameter(name = "id", description = "Post id"),
            @Parameter(name = "parentCommentId", description = "Parent comment id")
        }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found post comments page",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid post id or parent comment id or no pagination settings were given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post or parent comment were not found",
            content = @Content
        )
    })
    @GetMapping("/{id}/comments")
    ResponseEntity<Page<CommentData>> getPostCommentsPage(
        @PathVariable("id") String postIdStr,
        @RequestParam(name = "parentCommentId", required = false) String parentCommentIdStr,
        Pageable pageable
    ){
        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);
        UUID parentCommentId = null;

        if(parentCommentIdStr != null){
            parentCommentId = uuidMapper.strToUUID(parentCommentIdStr, COMMENT_MAPPER_MESSAGE);
        }

        Page<CommentEntity> foundCommentsPage = commentService.getPostCommentsPage(postId, parentCommentId, pageable);
        Page<CommentData> convertedCommentsPage = foundCommentsPage.map(commentMapper::commentEntityToCommentData);

        return ResponseEntity.ok(convertedCommentsPage);
    }

    @Operation(
        summary = "Create post comment by post id and optional parent comment id",
        parameters = {
            @Parameter(name = "id", description = "Post id"),
            @Parameter(name = "parentCommentId", description = "Parent comment id")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New post data")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Post comment was created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "New comment data", implementation = CommentData.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid post id or parent comment id were given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Post or parent comment were not found",
            content = @Content
        )
    })
    @PostMapping("/{id}/comments")
    ResponseEntity<CommentData> createPostComment(
        @PathVariable("id") String postIdStr,
        @RequestParam(required = false, name = "parentCommentId") String parentCommentIdStr,
        @RequestBody CreateComment createComment
    ){
        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        Optional<UUID> parentCommentIdOpt = Optional.ofNullable(
            uuidMapper.strToUUID(parentCommentIdStr, COMMENT_MAPPER_MESSAGE)
        );

        CommentEntity createdComment = commentService.createComment(
            postId,
            parentCommentIdOpt,
            createComment.content()
        );
        CommentData createdCommentData = commentMapper.commentEntityToCommentData(createdComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommentData);
    }

    @Operation(
        summary = "Update comment by its id",
        parameters = @Parameter(name = "id", description = "Comment id"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New comment's data")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comment data were updated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(description = "Comment with updated data", implementation = CommentData.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid comment id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Logged user is not an author of comment",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment was not found",
            content = @Content
        )
    })
    @PutMapping("/comments/{id}")
    ResponseEntity<CommentData> updateComment(@PathVariable("id") String commentIdStr, @RequestBody UpdateComment updateComment){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MAPPER_MESSAGE);

        CommentEntity changedComment = commentService.updateComment(commentId, updateComment.content());
        CommentData changedCommentData = commentMapper.commentEntityToCommentData(changedComment);

        return ResponseEntity.ok(changedCommentData);
    }

    @Operation(
        summary = "Delete comment by its id",
        parameters = @Parameter(name = "id", description = "Comment id")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Comment was deleted",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid comment id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Logged user is not an author of given comment",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment was not found",
            content = @Content
        )
    })
    @DeleteMapping("/comments/{id}")
    ResponseEntity<Void> removeComment(@PathVariable("id") String commentIdStr){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MAPPER_MESSAGE);

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }
}
