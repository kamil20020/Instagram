package pl.instagram.instagram.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.*;
import static org.springframework.data.jpa.domain.Specification.allOf;
import static pl.instagram.instagram.specification.UserSpecification.*;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByAccountId(String accountId);
    boolean existsByNicknameContainingIgnoreCase(String nickname);
    boolean existsByIdAndLikedPostsId(UUID userId, UUID postId);
    boolean existsByAccountIdAndLikedPostsId(String accountId, UUID postId);
    boolean existsByAccountIdAndLikedCommentsId(String accountId, UUID commentId);
    Optional<UserEntity> findByAccountId(String accountId);
    Page<UserEntity> findByLikedPostsId(UUID postId, Pageable pageable);
    Page<UserEntity> findByFollowersUsersFollowedId(UUID followedId, Pageable pageable);
    Page<UserEntity> findByFollowedUsersFollowerId(UUID followerId, Pageable pageable);
    Page<UserEntity> findByLikedCommentsId(UUID commentId, Pageable pageable);

    default Page<UserEntity> searchByFirstnameAndSurname(String phrase, Pageable pageable){

        String[] splitInput = phrase.split("\\s");

        if(splitInput.length < 2){
            return Page.empty();
        }

        return findAll(
            where(
                anyOf(
                    allOf(
                        userAboutFirstname(splitInput[0]),
                        userAboutSurname(splitInput[1])
                    ),
                    allOf(
                        userAboutFirstname(splitInput[1]),
                        userAboutSurname(splitInput[0])
                    )
                )
            ),
            pageable
        );
    }

    default Page<UserEntity> searchByFirstnameOrSurnameOrNickname(String phrase, Pageable pageable){

        return findAll(
            where(
                anyOf(
                    userAboutNickname(phrase),
                    userAboutFirstname(phrase),
                    userAboutSurname(phrase)
                )
            ),
            pageable
        );
    }

}
