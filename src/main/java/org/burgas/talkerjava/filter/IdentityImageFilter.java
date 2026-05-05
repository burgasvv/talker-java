package org.burgas.talkerjava.filter;

import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dto.document.ImageRequest;
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
public class IdentityImageFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        if (
                request.path().equals("/api/v1/identity-images/create") ||
                request.path().equals("/api/v1/identity-images/delete")
        ) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();
            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(identityId)) {
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/identity-images/make-preview")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ImageRequest imageRequest = request.body(ImageRequest.class);
                UUID entityId = Objects.requireNonNull(imageRequest.getEntityId());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(entityId)) {
                    request.attributes().put("imageRequest", imageRequest);
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
