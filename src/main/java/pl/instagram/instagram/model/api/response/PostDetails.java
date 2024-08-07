package pl.instagram.instagram.model.api.response;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetails {

    private String id;
    private OffsetDateTime creationDatetime;
    private String description;
    private String img;
    private boolean areHiddenLikes;
    private boolean areDisabledComments;
    private UserHeader userData;
    private Integer numberOfLikes;
    private Integer numberOfComments;
}
