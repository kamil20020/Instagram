package pl.instagram.instagram.model.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePost (

    @NotBlank(message = "Opis jest wymagany")
    String description,

    @NotNull(message = "Zawartość postu nie może być pusta")
    String content,

    boolean areHiddenLikes,
    boolean areDisabledComments
){}
