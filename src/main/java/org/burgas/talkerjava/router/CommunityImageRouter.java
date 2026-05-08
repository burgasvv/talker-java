package org.burgas.talkerjava.router;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dao.community.CommunityImage;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.burgas.talkerjava.dto.document.ImageRequest;
import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.burgas.talkerjava.filter.CommunityImageFilter;
import org.burgas.talkerjava.service.CommunityImageService;
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
public class CommunityImageRouter {

    @Bean
    public RouterFunction<ServerResponse> communityImageRoutes(
            CommunityImageService communityImageService, CommunityImageFilter communityImageFilter
    ) {
        return RouterFunctions.route()
                .filter(communityImageFilter)
                .GET(
                        "/api/v1/community-images/by-id", request -> {
                            UUID imageId = UUID.fromString(request.param("imageId").orElseThrow());
                            CommunityImage image = communityImageService.findEntity(imageId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(image.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
                        }
                )
                .POST(
                        "/api/v1/community-images/create", request -> {
                            UUID communityId = UUID.fromString(request.param("communityId").orElseThrow());
                            List<Part> parts = request.multipartData().get("image");
                            communityImageService.create(communityId, parts);
                            return ServerResponse.noContent().build();
                        }
                )
                .DELETE(
                        "/api/v1/community-images/delete", request -> {
                            UUID communityId = UUID.fromString(request.param("communityId").orElseThrow());
                            DocumentRequest documentRequest = request.body(DocumentRequest.class);
                            communityImageService.delete(communityId, documentRequest);
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/community-images/make-preview", request -> {
                            ImageRequest imageRequest = request.body(ImageRequest.class);
                            communityImageService.makePreview(imageRequest);
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
