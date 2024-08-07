package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByAccountId(String accountId);
    Optional<UserEntity> findByAccountId(String accountId);
    Page<UserEntity> findByLikedPostsId(UUID postId, Pageable pageable);
    boolean existsByIdAndLikedPostsId(UUID userId, UUID postId);
}
