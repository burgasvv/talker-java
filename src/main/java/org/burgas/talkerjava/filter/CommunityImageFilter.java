package org.burgas.talkerjava.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.repository.CommunityRepository;
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
public class CommunityImageFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CommunityRepository communityRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        if (
                request.path().equals("/api/v1/community-images/create") ||
                request.path().equals("/api/v1/community-images/delete")
        ) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID communityId = UUID.fromString(request.param("communityId").orElseThrow());
                Community community = communityRepository.findById(communityId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(community.getAdmin().getId())) {
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/community-images/make-preview")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                ImageRequest imageRequest = request.body(ImageRequest.class);
                UUID communityId = Objects.requireNonNull(imageRequest.getEntityId());
                Community community = communityRepository.findById(communityId).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(community.getAdmin().getId())) {
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
