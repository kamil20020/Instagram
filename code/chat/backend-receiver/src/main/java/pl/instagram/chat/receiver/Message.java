package pl.instagram.chat.receiver;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record Message(
    UUID id,
    String senderAccountId,
    String receiverAccountId,
    String content,
    LocalDateTime creationDate,
    boolean read
){}
