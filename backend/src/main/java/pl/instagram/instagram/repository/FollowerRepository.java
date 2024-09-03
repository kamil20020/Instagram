package pl.instagram.instagram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.FollowerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowerRepository extends JpaRepository<FollowerEntity, FollowerEntity.FollowerEntityId> {

    boolean existsByFollowerAccountIdAndFollowedId(String followerAccountId, UUID followedId);
    Optional<FollowerEntity> findByFollowerAccountIdAndFollowedId(String followerAccountId, UUID followedId);
}
