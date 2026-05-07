package org.burgas.talkerjava.router;

import org.burgas.talkerjava.dto.community.CommunityFullResponse;
import org.burgas.talkerjava.dto.community.CommunityRequest;
import org.burgas.talkerjava.dto.community.CommunityShortResponse;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.filter.CommunityFilter;
import org.burgas.talkerjava.service.CommunityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Configuration
public class CommunityRouter {

    @Bean
    public RouterFunction<ServerResponse> communityRoutes(CommunityService communityService, CommunityFilter communityFilter) {
        return RouterFunctions.route()
                .filter(communityFilter)
                .GET(
                        "/api/v1/communities", _ -> {
                            List<CommunityShortResponse> all = communityService.findAll();
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(all);
                        }
                )
                .GET(
                        "/api/v1/communities/by-id", request -> {
                            UUID communityId = UUID.fromString(request.param("communityId").orElseThrow());
                            CommunityFullResponse communityFullResponse = communityService.findById(communityId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(communityFullResponse);
                        }
                )
                .POST(
                        "/api/v1/communities/create", request -> {
                            CommunityRequest communityRequest = (CommunityRequest) request.attribute("communityRequest").orElseThrow();
                            CommunityFullResponse communityFullResponse = communityService.create(communityRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/communities/by-id?communityId=" + communityFullResponse.getId()))
                                    .build();
                        }
                )
                .POST(
                        "/api/v1/communities/update", request -> {
                            CommunityRequest communityRequest = (CommunityRequest) request.attribute("communityRequest").orElseThrow();
                            CommunityFullResponse communityFullResponse = communityService.update(communityRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/communities/by-id?communityId=" + communityFullResponse.getId()))
                                    .build();
                        }
                )
                .DELETE(
                        "/api/v1/communities/delete", request -> {
                            UUID communityId = UUID.fromString(request.param("communityId").orElseThrow());
                            communityService.delete(communityId);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/communities/join", request -> {
                            GroupRequest groupRequest = (GroupRequest) request.attribute("groupRequest").orElseThrow();
                            communityService.join(groupRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/communities/out", request -> {
                            GroupRequest groupRequest = (GroupRequest) request.attribute("groupRequest").orElseThrow();
                            communityService.out(groupRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
