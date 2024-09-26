package pl.instagram.chat.receiver;

import java.io.Serializable;
import java.util.UUID;

public record Message(
    UUID id,
    String senderAccountId,
    String receiverAccountId
){}
