package pl.instagram.instagram.model.api.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileInfo {

    private String userId;
    private String nickname;
    private String firstname;
    private String surname;
    private String avatar;
    private boolean isVerified;
    private boolean isPrivate;
    private String description;
    private Integer followers;
    private Integer followings;
    private Integer numberOfPosts;
}
