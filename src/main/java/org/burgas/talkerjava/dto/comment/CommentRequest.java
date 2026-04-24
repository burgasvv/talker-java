package org.burgas.talkerjava.dto.comment;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest implements Request {

    private UUID id;
    private UUID publicationId;
    private UUID senderId;
    private String text;
}
