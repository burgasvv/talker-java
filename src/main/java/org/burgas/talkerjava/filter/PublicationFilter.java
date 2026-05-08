package org.burgas.talkerjava.filter;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dao.publication.Publication;
import org.burgas.talkerjava.dto.publication.PublicationRequest;
import org.burgas.talkerjava.repository.PublicationRepository;
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
public class PublicationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final PublicationRepository publicationRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        if (request.path().equals("/api/v1/publications/create")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ObjectMapper objectMapper = new ObjectMapper();
                Part publicationPart = request.multipartData().getFirst("publicationRequest");
                List<Part> files = request.multipartData().get("file");

                assert publicationPart != null;
                PublicationRequest publicationRequest = objectMapper.readValue(
                        publicationPart.getInputStream().readAllBytes(), PublicationRequest.class
                );

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(publicationRequest.getSenderId())) {
                    request.attributes().put("publicationRequest", publicationRequest);
                    request.attributes().put("files", files);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (
                request.path().equals("/api/v1/publications/by-id") ||
                request.path().equals("/api/v1/publications/delete")
        ) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID publicationId = UUID.fromString(request.param("publicationId").orElseThrow());
                Publication publication = publicationRepository.findById(publicationId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(publication.getSender().getId())) {
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
