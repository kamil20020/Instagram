package pl.instagram.instagram.model.api.response;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentData {

    private String id;
    private UserHeader userData;
    private String content;
    private OffsetDateTime creationDatetime;
}
