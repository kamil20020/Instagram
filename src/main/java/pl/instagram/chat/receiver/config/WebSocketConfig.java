package pl.instagram.chat.receiver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String brokerHost;

    @Value("${rabbitmq.stomp.port}")
    private int brokerStompPort;

    @Value("${spring.rabbitmq.username}")
    private String brokerLogin;

    @Value("${spring.rabbitmq.password}")
    private String brokerPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config
            .enableStompBrokerRelay("/queue", "/topic")
            .setRelayHost(brokerHost)
            .setRelayPort(brokerStompPort)
            .setClientLogin(brokerLogin)
            .setClientPasscode(brokerPassword);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*");
    }
}
