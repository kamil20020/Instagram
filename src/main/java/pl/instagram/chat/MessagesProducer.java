package pl.instagram.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.instagram.chat.models.MessageEntity;
import pl.instagram.chat.models.MessageResponse;

@Component
@RequiredArgsConstructor
public class MessagesProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.chat.routing-key}")
    private String chatRoutingKey;

    public void sendMessage(String message){

        rabbitTemplate.convertAndSend(chatRoutingKey, message);
    }
}
