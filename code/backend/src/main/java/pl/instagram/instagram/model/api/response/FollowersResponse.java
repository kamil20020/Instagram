package pl.instagram.instagram.model.api.response;

public record FollowersResponse(
    RestPage<UserHeader> followers,
    boolean didLoggedUserFollowed
){}
