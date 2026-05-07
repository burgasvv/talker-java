package org.burgas.talkerjava.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.community.Community;
import org.burgas.talkerjava.dao.identity.Identity;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.dto.community.CommunityRequest;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.repository.CommunityRepository;
import org.burgas.talkerjava.repository.IdentityRepository;
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
public class CommunityFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final CommunityRepository communityRepository;
    private final IdentityRepository identityRepository;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next)
            throws Exception {

        if (request.path().equals("/api/v1/communities/create")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                CommunityRequest communityRequest = request.body(CommunityRequest.class);
                UUID adminId = Objects.requireNonNull(communityRequest.getAdminId());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(adminId)) {
                    request.attributes().put("communityRequest", communityRequest);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/communities/update")) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                CommunityRequest communityRequest = request.body(CommunityRequest.class);
                Community community = communityRepository.findById(Objects.requireNonNull(communityRequest.getId())).orElseThrow();

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(community.getAdmin().getId())) {
                    request.attributes().put("communityRequest", communityRequest);
                    return next.handle(request);
                } else {
                    throw new IllegalArgumentException("Identity not authorized");
                }

            } else {
                throw new IllegalArgumentException("Identity not authenticated");
            }

        } else if (request.path().equals("/api/v1/communities/delete")) {
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

        } else if (
                request.path().equals("/api/v1/communities/join") ||
                request.path().equals("/api/v1/communities/out")
        ) {
            Authentication authentication = (Authentication) request.principal().orElseThrow();

            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                GroupRequest groupRequest = request.body(GroupRequest.class);
                Identity identity = identityRepository.findById(Objects.requireNonNull(groupRequest.getApplicantId())).orElseThrow();

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
