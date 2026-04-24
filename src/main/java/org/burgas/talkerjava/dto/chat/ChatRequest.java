package org.burgas.talkerjava.dto.chat;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest implements Request {

    private UUID id;
    private String name;
    private String description;
    private UUID adminId;
}
