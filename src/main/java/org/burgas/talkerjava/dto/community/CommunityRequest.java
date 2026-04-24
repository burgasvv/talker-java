package org.burgas.talkerjava.dto.community;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRequest implements Request {

    private UUID id;
    private String name;
    private String description;
    private UUID adminId;
}
