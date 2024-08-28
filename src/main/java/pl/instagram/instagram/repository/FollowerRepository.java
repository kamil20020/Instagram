package pl.instagram.instagram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.FollowerEntity;

@Repository
public interface FollowerRepository extends JpaRepository<FollowerEntity, FollowerEntity.FollowerEntityId> {

}
