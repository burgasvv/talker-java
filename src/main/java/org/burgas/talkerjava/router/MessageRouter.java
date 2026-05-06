package org.burgas.talkerjava.router;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.dto.message.MessageFullResponse;
import org.burgas.talkerjava.dto.message.MessageRequest;
import org.burgas.talkerjava.filter.MessageFilter;
import org.burgas.talkerjava.service.MessageService;
import org.burgas.talkerjava.websocket.MessageWebSocketHandler;
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
public class MessageRouter {

    @Bean
    public RouterFunction<ServerResponse> messageRoutes(
            MessageService messageService, MessageFilter messageFilter, MessageWebSocketHandler messageWebSocketHandler
    ) {
        return RouterFunctions.route()
                .filter(messageFilter)
                .GET(
                        "/api/v1/messages/by-id", request -> {
                            UUID messageId = UUID.fromString(request.param("messageId").orElseThrow());
                            MessageFullResponse messageFullResponse = messageService.findById(messageId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(messageFullResponse);
                        }
                )
                .POST(
                        "/api/v1/messages/create", request -> {
                            MessageRequest messageRequest = (MessageRequest) request.attribute("messageRequest").orElseThrow();
                            @SuppressWarnings("unchecked") List<Part> files = (List<Part>) request.attribute("files").orElseThrow();
                            MessageFullResponse messageFullResponse = messageService.create(messageRequest, files);
                            ObjectMapper objectMapper = new ObjectMapper();
                            String message = objectMapper.writeValueAsString(messageFullResponse);
                            messageWebSocketHandler.broadcast(message);
                            return ServerResponse.noContent().build();
                        }
                )
                .DELETE(
                        "/api/v1/messages/delete", request -> {
                            UUID messageId = UUID.fromString(request.param("messageId").orElseThrow());
                            messageService.delete(messageId);
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
