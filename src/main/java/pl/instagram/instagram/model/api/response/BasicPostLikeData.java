package pl.instagram.instagram.model.api.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicPostLikeData {

    private String id;
    private BasicUserData user;
}
