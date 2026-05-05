package org.burgas.talkerjava.dto.exception;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    private String status;
    private Integer code;
    private String message;
}
