package pl.instagram.instagram.model.api.response;

import lombok.NoArgsConstructor;

public record UserHeader (
    String id,
    String nickname,
    String firstname,
    String surname,
    String avatar,
    boolean isVerified
){}
