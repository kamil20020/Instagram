package pl.instagram.chat.models;

import java.util.UUID;

public record MessageResponse(
    UUID id,
    String senderAccountId,
    String receiverAccountId
){}
