package pl.instagram.instagram.model.api.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUser {

    private String nickname;
    private String firstname;
    private String surname;
    private String avatar;
}
