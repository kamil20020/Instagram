package pl.instagram.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.instagram.chat.models.MessageEntity;

@Component
@RequiredArgsConstructor
public class MessagesProducerConfig {

    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.chat.routing-key}")
    private String chatRoutingKey;

    public void sendMessage(MessageEntity message){

        rabbitTemplate.convertAndSend(chatRoutingKey, message);
    }
}
