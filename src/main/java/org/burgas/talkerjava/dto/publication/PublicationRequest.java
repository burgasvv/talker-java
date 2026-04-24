package org.burgas.talkerjava.dto.publication;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationRequest implements Request {

    private UUID id;
    private UUID communityId;
    private UUID senderId;
    private String text;
}
