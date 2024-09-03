package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.CommentLikeEntity;

import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository <CommentLikeEntity, CommentLikeEntity.CommentLikeEntityId> {

    boolean existsByAuthorAccountIdAndCommentId(String accountId, UUID commentId);
    Page<CommentLikeEntity> findByCommentId(UUID commentId, Pageable pageable);
}
