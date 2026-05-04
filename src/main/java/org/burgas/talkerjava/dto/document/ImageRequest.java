package org.burgas.talkerjava.dto.document;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest implements Request {

    private UUID entityId;
    private UUID imageId;
}
