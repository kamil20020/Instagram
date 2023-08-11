package pl.instagram.instagram.model.api.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostHeader {

    private String id;
    private String img;
}
