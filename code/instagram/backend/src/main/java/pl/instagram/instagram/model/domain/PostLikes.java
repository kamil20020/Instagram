package pl.instagram.instagram.model.domain;

import org.springframework.data.domain.Page;
import pl.instagram.instagram.model.entity.UserEntity;

import java.util.UUID;

public record PostLikes(
    UUID postId,
    Page<UserEntity> postLikes,
    boolean didLoggedUserLikePost
){}
