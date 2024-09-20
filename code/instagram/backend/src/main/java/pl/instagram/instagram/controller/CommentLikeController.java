package pl.instagram.instagram.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.CommentLikeService;

import java.util.UUID;

@RestController
@CrossOrigin(value = "*")
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    private final UUIDMapper uuidMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    private static final String COMMENT_MESSAGE = "komentarza";

    @Operation(summary = "Get comment likes page - users who liked comment")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found comment likes page",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid comment id or no pagination were given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment was not found",
            content = @Content
        )
    })
    @GetMapping("/{commentId}/likes")
    public ResponseEntity<Page<UserHeader>> getCommentLikesPage(@PathVariable("commentId") String commentIdStr, Pageable pageable){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MESSAGE);

        Page<UserEntity> gotCommentLikesPage = commentLikeService.getCommentLikesPage(commentId, pageable);
        Page<UserHeader> gotCommentConvertedLikesPage = gotCommentLikesPage.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(gotCommentConvertedLikesPage);
    }

    @Operation(summary = "Create comment like by logged user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Comment like's author info",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserHeader.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid comment id was given",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment was not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already likes given comment",
            content = @Content
        )
    })
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<UserHeader> createCommentLike(@PathVariable("commentId") String commentIdStr){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MESSAGE);

        UserEntity likeAuthor = commentLikeService.createCommentLike(commentId);
        UserHeader likeAuthorHeader = userMapper.userEntityToUserHeader(likeAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeAuthorHeader);
    }

    @Operation(summary = "Delete comment like by logged user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Comment like was deleted",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment or comment like were not found",
            content = @Content
        )
    })
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<Void> deleteCommentLike(@PathVariable("commentId") String commentIdStr){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MESSAGE);

        commentLikeService.deleteCommentLike(commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}