package pl.instagram.instagram.model.domain;

import org.springframework.data.domain.Page;
import pl.instagram.instagram.model.entity.UserEntity;

public record Followers(
    Page<UserEntity> followers,
    boolean didLoggedUserFollowed
){}
