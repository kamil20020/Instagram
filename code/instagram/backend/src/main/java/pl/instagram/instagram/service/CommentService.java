package pl.instagram.instagram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.model.entity.CommentEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    Page<CommentEntity> getPostCommentsPage(UUID postId, Pageable pageable);
    Page<CommentEntity> getSubCommentsPage(UUID parentCommentId, Pageable pageable);
    CommentEntity createComment(UUID postId, UUID userId, UUID parentCommentId, String content);
    CommentEntity updateComment(UUID commentId, String newContent);
    void deleteComment(UUID commentId);
}
