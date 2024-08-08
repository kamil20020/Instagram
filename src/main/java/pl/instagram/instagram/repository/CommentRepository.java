package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.CommentEntity;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    Page<CommentEntity> getAllByPostIdAndParentCommentId(UUID postId, UUID parentCommentId, Pageable pageable);
}
