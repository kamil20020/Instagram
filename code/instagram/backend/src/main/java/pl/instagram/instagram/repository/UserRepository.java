package pl.instagram.instagram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {


}
