package com.example.Qatu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefijo para los canales de suscripción (cliente escucha aquí)
        registry.enableSimpleBroker("/topic", "/user");

        // Prefijo para mensajes que van al servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Prefijo para mensajes privados (por usuario)
        registry.setUserDestinationPrefix("/user");
    }

   @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Con SockJS (para Flutter/React en producción)
        registry.addEndpoint("/ws/mapa")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Sin SockJS (para pruebas con Postman)
        registry.addEndpoint("/ws/mapa-native")
                .setAllowedOriginPatterns("*");
    }
}