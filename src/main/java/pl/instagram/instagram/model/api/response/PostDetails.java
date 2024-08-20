package pl.instagram.instagram.model.api.response;

import java.time.OffsetDateTime;

public record PostDetails (
    String id,
    OffsetDateTime creationDatetime,
    String description,
    String content,
    boolean areHiddenLikes,
    boolean areDisabledComments,
    UserHeader author,
    Integer likesCount,
    Integer commentsCount
){}
