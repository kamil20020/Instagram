package pl.instagram.instagram.model.api.response;

public record UserProfile(
    String id,
    String nickname,
    String firstname,
    String surname,
    String avatar,
    boolean isVerified,
    boolean isPrivate,
    String description,
    Integer followers,
    Integer followings,
    Integer numberOfPosts
){}
