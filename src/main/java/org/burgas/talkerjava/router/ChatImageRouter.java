package org.burgas.talkerjava.router;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dao.chat.ChatImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.filter.ChatImageFilter;
import org.burgas.talkerjava.service.ChatImageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Configuration
public class ChatImageRouter {

    @Bean
    public RouterFunction<ServerResponse> chatImageRoutes(ChatImageService chatImageService, ChatImageFilter chatImageFilter) {
        return RouterFunctions.route()
                .filter(chatImageFilter)
                .GET(
                        "/api/v1/chat-images/by-id", request -> {
                            UUID imageId = UUID.fromString(request.param("imageId").orElseThrow());
                            ChatImage image = chatImageService.findEntity(imageId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(image.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
                        }
                )
                .POST(
                        "/api/v1/chat-images/create", request -> {
                            UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                            List<Part> parts = request.multipartData().get("image");
                            chatImageService.create(chatId, parts);
                            return ServerResponse.noContent().build();
                        }
                )
                .DELETE(
                        "/api/v1/chat-images/delete", request -> {
                            UUID chatId = UUID.fromString(request.param("chatId").orElseThrow());
                            DocumentRequest documentRequest = request.body(DocumentRequest.class);
                            chatImageService.delete(chatId, documentRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/chat-images/make-preview", request -> {
                            ImageRequest imageRequest = request.body(ImageRequest.class);
                            chatImageService.makePreview(imageRequest);
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
