package org.burgas.talkerjava.filter;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dao.message.Message;
import org.burgas.talkerjava.dto.message.MessageRequest;
import org.burgas.talkerjava.repository.MessageRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final MessageRepository messageRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        if (request.path().equals("/api/v1/messages/create")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ObjectMapper objectMapper = new ObjectMapper();
                Part messagePart = request.multipartData().getFirst("messageRequest");
                List<Part> files = request.multipartData().get("file");
                assert messagePart != null;
                MessageRequest messageRequest = objectMapper.readValue(messagePart.getInputStream().readAllBytes(), MessageRequest.class);

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(messageRequest.getSenderId())) {
                    request.attributes().put("messageRequest", messageRequest);
                    request.attributes().put("files", files);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }
            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/messages/delete")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID messageId = UUID.fromString(request.param("messageId").orElseThrow());
                Message message = messageRepository.findById(messageId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(message.getSender().getId())) {
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }
            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else {
            return next.handle(request);
        }
    }
}
