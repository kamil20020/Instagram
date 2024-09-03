package pl.instagram.instagram.model.api.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateComment (

    @NotBlank(message = "Treść komentarza nie może byc pusta")
    String content
){}
