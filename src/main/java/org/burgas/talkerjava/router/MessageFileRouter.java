package org.burgas.talkerjava.router;

import org.burgas.talkerjava.dao.message.MessageFile;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.service.MessageFileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Configuration
public class MessageFileRouter {

    @Bean
    public RouterFunction<ServerResponse> messageFileRoutes(MessageFileService messageFileService) {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/message-files/by-id", request -> {
                            UUID fileId = UUID.fromString(request.param("fileId").orElseThrow());
                            MessageFile messageFile = messageFileService.findEntity(fileId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(messageFile.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(messageFile.getData())));
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
