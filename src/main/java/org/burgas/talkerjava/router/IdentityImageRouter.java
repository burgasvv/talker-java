package org.burgas.talkerjava.router;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.filter.IdentityImageFilter;
import org.burgas.talkerjava.service.IdentityImageService;
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
public class IdentityImageRouter {

    @Bean
    public RouterFunction<ServerResponse> identityImageRoutes(
            IdentityImageService identityImageService, IdentityImageFilter identityImageFilter
    ) {
        return RouterFunctions.route()
                .filter(identityImageFilter)
                .GET(
                        "/api/v1/identity-images/by-id", request -> {
                            UUID imageId = UUID.fromString(request.param("imageId").orElseThrow());
                            IdentityImage image = identityImageService.findEntity(imageId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(image.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
                        }
                )
                .POST(
                        "/api/v1/identity-images/create", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            List<Part> parts = request.multipartData().get("image");
                            identityImageService.create(identityId, parts);
                            return ServerResponse.noContent().build();
                        }
                )
                .DELETE(
                        "/api/v1/identity-images/delete", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            DocumentRequest documentRequest = request.body(DocumentRequest.class);
                            identityImageService.delete(identityId, documentRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/identity-images/make-preview", request -> {
                            ImageRequest imageRequest = (ImageRequest) request.attribute("imageRequest").orElseThrow();
                            identityImageService.makePreview(imageRequest);
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
