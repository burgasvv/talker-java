package org.burgas.talkerjava.dto.message;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest implements Request {

    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String text;
}
