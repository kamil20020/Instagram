package pl.instagram.chat.receiver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagesListener {

    @RabbitListener(queues = "${rabbitmq.chat.queue.name}")
    public void handleReceiveMessage(String message){

        log.info(message);
    }
}
