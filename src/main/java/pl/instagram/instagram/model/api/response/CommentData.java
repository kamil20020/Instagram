package pl.instagram.instagram.model.api.response;

import java.time.OffsetDateTime;

public record CommentData(
    String id,
    UserHeader author,
    String content,
    OffsetDateTime creationDatetime,
    Integer subCommentsCount,
    Integer likesCount,
    boolean didLoggedUserLikeComment
){
    public CommentData(String id, UserHeader author, String content, OffsetDateTime creationDatetime, Integer subCommentsCount, Integer likesCount) {
        this(id, author, content, creationDatetime, subCommentsCount, likesCount, false);
    }
}
