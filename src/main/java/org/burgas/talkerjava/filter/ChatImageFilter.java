package org.burgas.talkerjava.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.repository.ChatRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatImageFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final ChatRepository chatRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        Authentication authentication = (Authentication) request.principal().orElseThrow();
        if (authentication.isAuthenticated()) {
            IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();

            if (
                    request.path().equals("/api/v1/chat-images/create") ||
                    request.path().equals("/api/v1/chat-images/delete")
            ) {
                UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                Chat chat = chatRepository.findById(chatId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(chat.getAdmin().getId())) {
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else if (request.path().equals("/api/v1/chat-images/make-preview")) {
                ImageRequest imageRequest = request.body(ImageRequest.class);
                UUID identityId = Objects.requireNonNull(imageRequest.getEntityId());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(identityId)) {
                    request.attributes().put("imageRequest", imageRequest);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                return next.handle(request);
            }

        } else {
            throw new IllegalArgumentException("Identity not authenticated");
        }
    }
}
