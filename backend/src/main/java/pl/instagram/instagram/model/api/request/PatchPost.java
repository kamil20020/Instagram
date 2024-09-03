package pl.instagram.instagram.model.api.request;

public record PatchPost(
    String description,
    Boolean areHiddenLikes,
    Boolean areDisabledComments
) {}
