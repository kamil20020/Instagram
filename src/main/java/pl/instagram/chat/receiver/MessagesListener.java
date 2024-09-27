package pl.instagram.chat.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagesListener {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SimpMessagingTemplate messagingTemplate;

    {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @RabbitListener(queues = "${rabbitmq.chat.queue.name}")
    public void handleReceiveMessage(String message) throws JsonProcessingException {

        Message convertedMessage = objectMapper.readValue(message, Message.class);

        log.info("Message {}", convertedMessage);

        log.info("Sending message to user {}", convertedMessage);

        messagingTemplate.convertAndSend("/queue/" + convertedMessage.receiverAccountId(), convertedMessage);
    }
}
