package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.PostEntity;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    boolean existsByIdAndAuthorAccountId(UUID id, String authorAccountId);
    Page<PostEntity> findAllByAuthorId(UUID authorId, Pageable pageable);
}
