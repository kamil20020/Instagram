package pl.instagram.chat.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

public record CreateMessage(

    @NotNull(message = "Is required")
    String receiverAccountId,

    @NotBlank(message = "Is required")
    String content
){}
