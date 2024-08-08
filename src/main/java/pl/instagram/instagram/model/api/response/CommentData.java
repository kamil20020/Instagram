package pl.instagram.instagram.model.api.response;

import java.time.OffsetDateTime;

public record CommentData(
    String id,
    UserHeader author,
    String content,
    OffsetDateTime creationDatetime
){}
