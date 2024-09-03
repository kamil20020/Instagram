package pl.instagram.instagram.model.domain;

public record PatchPostData(
    String description,
    Boolean areHiddenLikes,
    Boolean areDisabledComments
) {}
