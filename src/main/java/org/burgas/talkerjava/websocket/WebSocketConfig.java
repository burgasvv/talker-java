package org.burgas.talkerjava.websocket;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageWebSocketHandler messageWebSocketHandler;
    private final PublicationWebSocketHandler publicationWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry
                .addHandler(messageWebSocketHandler, "/api/v1/messages/ws/by-chat")
                .addHandler(publicationWebSocketHandler, "/api/v1/publications/ws/by-community")
                .setAllowedOrigins("http://localhost:9000");
    }
}
