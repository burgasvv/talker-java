package org.burgas.talkerjava.router;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.dto.publication.PublicationFullResponse;
import org.burgas.talkerjava.dto.publication.PublicationRequest;
import org.burgas.talkerjava.filter.PublicationFilter;
import org.burgas.talkerjava.service.PublicationService;
import org.burgas.talkerjava.websocket.PublicationWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Configuration
public class PublicationRouter {

    @Bean
    public RouterFunction<ServerResponse> publicationRoutes(
            PublicationService publicationService, PublicationFilter publicationFilter, PublicationWebSocketHandler publicationWebSocketHandler
    ) {
        return RouterFunctions.route()
                .filter(publicationFilter)
                .GET(
                        "/api/v1/publications/by-id", request -> {
                            UUID publicationId = UUID.fromString(request.param("publicationId").orElseThrow());
                            PublicationFullResponse publicationFullResponse = publicationService.findById(publicationId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(publicationFullResponse);
                        }
                )
                .POST(
                        "/api/v1/publications/create", request -> {
                            PublicationRequest publicationRequest = (PublicationRequest) request
                                    .attribute("publicationRequest").orElseThrow();
                            @SuppressWarnings("unchecked") List<Part> parts = (List<Part>) request.attribute("files").orElseThrow();
                            var publicationFullResponse = publicationService.create(publicationRequest, parts);
                            ObjectMapper objectMapper = new ObjectMapper();
                            String message = objectMapper.writeValueAsString(publicationFullResponse);
                            publicationWebSocketHandler.broadcast(message);
                            return ServerResponse.noContent().build();
                        }
                )
                .DELETE(
                        "/api/v1/publications/delete", request -> {
                            UUID publicationId = UUID.fromString(request.param("publicationId").orElseThrow());
                            publicationService.delete(publicationId);
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
