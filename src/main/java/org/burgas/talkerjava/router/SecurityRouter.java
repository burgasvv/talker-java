package org.burgas.talkerjava.router;

import org.burgas.talkerjava.dto.exception.ExceptionResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class SecurityRouter {

    @Bean
    public RouterFunction<ServerResponse> securityRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/security/csrf-token", request -> {
                            CsrfToken csrf = (CsrfToken) request.attribute("_csrf").orElseThrow();
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(csrf);
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
