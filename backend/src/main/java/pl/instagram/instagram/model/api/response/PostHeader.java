package pl.instagram.instagram.model.api.response;

public record PostHeader (
    String id,
    String content,
    Integer likesCount,
    Integer commentsCount,
    boolean areHiddenLikes
){}