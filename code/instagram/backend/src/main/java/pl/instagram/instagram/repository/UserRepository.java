package pl.instagram.instagram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByUserAccountId(String userAccountId);
    Optional<UserEntity> findByUserAccountId(String userAccountId);
}
