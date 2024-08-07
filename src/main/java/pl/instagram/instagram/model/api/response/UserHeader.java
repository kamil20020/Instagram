package pl.instagram.instagram.model.api.response;

public record UserHeader (
    String id,
    String nickname,
    String firstname,
    String surname,
    String avatar,
    boolean isVerified
){}
