package org.burgas.talkerjava.router;

import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.dto.identity.IdentityFullResponse;
import org.burgas.talkerjava.dto.identity.IdentityRequest;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.filter.IdentityFilter;
import org.burgas.talkerjava.service.IdentityService;
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
public class IdentityRouter {

    @Bean
    public RouterFunction<ServerResponse> identityRoutes(IdentityService identityService, IdentityFilter identityFilter) {
        return RouterFunctions.route()
                .filter(identityFilter)
                .GET(
                        "/api/v1/identities", _ -> {
                            List<IdentityShortResponse> all = identityService.findAll();
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(all);
                        }
                )
                .GET(
                        "/api/v1/identities/by-id", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(identityService.findById(identityId));
                        }
                )
                .POST(
                        "/api/v1/identities/create", request -> {
                            IdentityRequest identityRequest = request.body(IdentityRequest.class);
                            IdentityFullResponse identityFullResponse = identityService.create(identityRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/identities/by-id?identityId=" + identityFullResponse.getId()))
                                    .build();
                        }
                )
                .POST(
                        "/api/v1/identities/update", request -> {
                            IdentityRequest identityRequest = (IdentityRequest) request.attribute("identityRequest").orElseThrow();
                            IdentityFullResponse identityFullResponse = identityService.update(identityRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/identities/by-id?identityId=" + identityFullResponse.getId()))
                                    .build();
                        }
                )
                .DELETE(
                        "/api/v1/identities/delete", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            identityService.delete(identityId);
                            return ServerResponse.noContent().build();
                        }
                )
                .onError(
                        Throwable.class, (throwable, _) -> {
                            var exceptionResponse = ExceptionResponse.builder()
                                    .status(HttpStatus.BAD_REQUEST.name())
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(throwable.getLocalizedMessage())
                                    .build();
                            return ServerResponse
                                    .status(HttpStatus.BAD_REQUEST)
                                    .body(exceptionResponse);
                        }
                )
                .build();
    }
}
