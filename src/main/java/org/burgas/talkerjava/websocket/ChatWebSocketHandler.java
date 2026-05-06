package org.burgas.talkerjava.websocket;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.mapper.MessageMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageMapper messageMapper;
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        sessions.forEach(webSocketSession -> {
            String chatStringId = Objects.requireNonNull(webSocketSession.getUri()).getQuery().split("=")[1];
            UUID chatId = UUID.fromString(chatStringId);
            List<MessageFullResponse> messageFullResponses = messageMapper.messageRepository.findMessagesByChatId(chatId)
                    .parallelStream()
                    .map(messageMapper::toFullResponse)
                    .toList();
            ObjectMapper objectMapper = new ObjectMapper();
            messageFullResponses.forEach(messageFullResponse ->
                    {
                        try {
                            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageFullResponse)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        });
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcast(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
