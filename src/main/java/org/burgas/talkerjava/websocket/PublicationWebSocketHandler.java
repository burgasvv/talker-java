package org.burgas.talkerjava.websocket;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.burgas.talkerjava.mapper.PublicationMapper;
import org.burgas.talkerjava.repository.CommunityRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PublicationWebSocketHandler extends TextWebSocketHandler {

    private final CommunityRepository communityRepository;
    private final PublicationMapper publicationMapper;
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        for (WebSocketSession webSocketSession : sessions) {
            String communityIdString = Objects.requireNonNull(webSocketSession.getUri()).getQuery().split("=")[1];
            UUID communityId = UUID.fromString(communityIdString);

            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new IllegalArgumentException("Community not found"));
            Set<PublicationFullResponse> publicationFullResponses = publicationMapper.publicationRepository
                    .findPublicationsByCommunity(community)
                    .parallelStream()
                    .map(publicationMapper::toFullResponse)
                    .collect(Collectors.toSet());

            ObjectMapper objectMapper = new ObjectMapper();
            for (PublicationFullResponse publicationFullResponse : publicationFullResponses) {
                String publicationAsString = objectMapper.writeValueAsString(publicationFullResponse);
                webSocketSession.sendMessage(new TextMessage(publicationAsString));
            }
        }
    }

    public void broadcast(String message) throws IOException {
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) webSocketSession.sendMessage(new TextMessage(message));
        }
    }
}
