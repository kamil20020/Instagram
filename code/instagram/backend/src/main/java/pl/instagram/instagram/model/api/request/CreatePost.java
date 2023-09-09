package pl.instagram.instagram.model.api.request;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePost {

    private String description;
    private String img;
    private boolean areHiddenLikes = false;
    private boolean areDisabledComments = false;
}
