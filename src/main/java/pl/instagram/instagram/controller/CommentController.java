package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.service.CommentService;

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

    @GetMapping("/{id}/comments")
    ResponseEntity<Page<CommentData>> getPostCommentsPage(
        @PathVariable("id") String postIdStr, @RequestParam(required = false) String parentCommentIdStr, Pageable pageable
    ){
        UUID postId = uuidMapper.strToUUID(postIdStr, COMMENT_MAPPER_MESSAGE);
        UUID parentCommentId = null;

        if(parentCommentIdStr != null){
            parentCommentId = uuidMapper.strToUUID(parentCommentIdStr, COMMENT_MAPPER_MESSAGE);
        }

        Page<CommentEntity> foundCommentsPage = commentService.getPostCommentsPage(postId, parentCommentId, pageable);
        Page<CommentData> convertedCommentsPage = foundCommentsPage.map(commentMapper::commentEntityToCommentData);

        return ResponseEntity.ok(convertedCommentsPage);
    }

    @PostMapping("/{id}/comments")
    ResponseEntity<CommentData> createPostComment(
        @PathVariable("id") String postIdStr,
        @RequestBody CreateComment createComment
    ){
        UUID postId = uuidMapper.strToUUID(postIdStr, COMMENT_MAPPER_MESSAGE);

        CommentEntity createdComment = commentService.createComment(
            postId,
            createComment.parentCommentId(),
            createComment.content()
        );
        CommentData createdCommentData = commentMapper.commentEntityToCommentData(createdComment);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommentData);
    }

    @PutMapping("/comments/{id}")
    ResponseEntity<CommentData> updateComment(@PathVariable("id") String commentIdStr, @RequestBody UpdateComment updateComment){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MAPPER_MESSAGE);

        CommentEntity changedComment = commentService.updateComment(commentId, updateComment.content());
        CommentData changedCommentData = commentMapper.commentEntityToCommentData(changedComment);

        return ResponseEntity.ok(changedCommentData);
    }

    @DeleteMapping("/comments/{id}")
    ResponseEntity<Void> removeComment(@PathVariable("id") String commentIdStr){

        UUID commentId = uuidMapper.strToUUID(commentIdStr, COMMENT_MAPPER_MESSAGE);

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }
}
