package pl.instagram.instagram.model.api.response;

import org.springframework.data.domain.Page;

public record PostLikesResponse(
    String postId,
    RestPage<UserHeader> postLikes,
    boolean didLoggedUserLikePost
){}
