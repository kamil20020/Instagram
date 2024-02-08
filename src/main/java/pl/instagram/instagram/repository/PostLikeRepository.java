package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.PostLike;

import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {

    Page<PostLike> getAllByPostEntityId(UUID postId, Pageable pageable);
    boolean existsByUserEntityIdAndPostEntityId(UUID userId, UUID postId);
}
