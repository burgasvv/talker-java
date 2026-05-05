package org.burgas.talkerjava.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.chat.Chat;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dto.chat.ChatRequest;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.repository.ChatRepository;
import org.burgas.talkerjava.repository.IdentityRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Objects;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ChatFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final ChatRepository chatRepository;
    private final IdentityRepository identityRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {
        if (request.path().equals("/api/v1/chats/create")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ChatRequest chatRequest = request.body(ChatRequest.class);
                UUID adminId = Objects.requireNonNull(chatRequest.getAdminId());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(adminId)) {
                    request.attributes().put("chatRequest", chatRequest);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/chats/update")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ChatRequest chatRequest = request.body(ChatRequest.class);
                UUID chatId = Objects.requireNonNull(chatRequest.getId());
                Chat chat = chatRepository.findById(chatId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(chat.getAdmin().getId())) {
                    request.attributes().put("chatRequest", chatRequest);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/chats/delete")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                Chat chat = chatRepository.findById(chatId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(chat.getAdmin().getId())) {
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (
                request.path().equals("/api/v1/chats/join") ||
                request.path().equals("/api/v1/chats/out")
        ) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                GroupRequest groupRequest = request.body(GroupRequest.class);

                UUID applicantId = Objects.requireNonNull(groupRequest.getApplicantId());
                Identity identity = identityRepository.findById(applicantId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(identity.getId())) {
                    request.attributes().put("groupRequest", groupRequest);
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
