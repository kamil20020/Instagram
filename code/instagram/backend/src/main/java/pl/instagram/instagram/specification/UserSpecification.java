package pl.instagram.instagram.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.instagram.instagram.model.entity.UserEntity;

public class UserSpecification {

    public static Specification<UserEntity> userAboutNickname(String nickname){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("nickname")),
                    "%" + nickname.toUpperCase().trim() + "%"
            );
    }

    public static Specification<UserEntity> userAboutFirstname(String firstname){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("firstname")),
                        "%" + firstname.toUpperCase().trim() + "%"
                );
    }

    public static Specification<UserEntity> userAboutSurname(String surname){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("surname")),
                        "%" + surname.toUpperCase().trim() + "%"
                );
    }
}
