package pl.instagram.instagram.model.api.request;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateComment {

    private String content;
    private UUID parentCommentId;
}
