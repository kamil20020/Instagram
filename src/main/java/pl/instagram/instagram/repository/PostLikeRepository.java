package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.PostLikeEntity;

import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository <PostLikeEntity, PostLikeEntity.PostLikeEntityId> {

    boolean existsByAuthorAccountIdAndPostId(String accountId, UUID postId);
    Page<PostLikeEntity> findByPostId(UUID postId, Pageable pageable);
}
