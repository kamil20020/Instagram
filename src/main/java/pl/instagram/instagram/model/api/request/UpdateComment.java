package pl.instagram.instagram.model.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateComment(
    String content
){}
