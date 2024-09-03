package pl.instagram.instagram.model.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateComment(

    @NotBlank(message = "Comment content is required")
    String content
){}
