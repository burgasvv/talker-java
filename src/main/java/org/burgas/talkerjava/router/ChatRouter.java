package org.burgas.talkerjava.router;

import org.burgas.talkerjava.dto.chat.ChatFullResponse;
import org.burgas.talkerjava.dto.chat.ChatRequest;
import org.burgas.talkerjava.dto.chat.ChatShortResponse;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.dto.group.GroupRequest;
import org.burgas.talkerjava.filter.ChatFilter;
import org.burgas.talkerjava.service.ChatService;
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
public class ChatRouter {

    @Bean
    public RouterFunction<ServerResponse> chatRoutes(ChatService chatService, ChatFilter chatFilter) {
        return RouterFunctions.route()
                .filter(chatFilter)
                .GET(
                        "/api/v1/chats", _ -> {
                            List<ChatShortResponse> all = chatService.findAll();
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(all);
                        }
                )
                .GET(
                        "/api/v1/chats/by-id", request -> {
                            UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(chatService.findById(chatId));
                        }
                )
                .POST(
                        "/api/v1/chats/create", request -> {
                            ChatRequest chatRequest = (ChatRequest) request.attribute("chatRequest").orElseThrow();
                            ChatFullResponse chatFullResponse = chatService.create(chatRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/chats/by-id?chatId=" + chatFullResponse.getId()))
                                    .build();
                        }
                )
                .POST(
                        "/api/v1/chats/update", request -> {
                            ChatRequest chatRequest = (ChatRequest) request.attribute("chatRequest").orElseThrow();
                            ChatFullResponse chatFullResponse = chatService.update(chatRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/chats/by-id?chatId=" + chatFullResponse.getId()))
                                    .build();
                        }
                )
                .DELETE(
                        "/api/v1/chats/delete", request -> {
                            UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                            chatService.delete(chatId);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/chats/join", request -> {
                            GroupRequest groupRequest = (GroupRequest) request.attribute("groupRequest").orElseThrow();
                            chatService.join(groupRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/chats/out", request -> {
                            GroupRequest groupRequest = (GroupRequest) request.attribute("groupRequest").orElseThrow();
                            chatService.out(groupRequest);
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
