package org.burgas.talkerjava.dto.document;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {

    private List<UUID> documentIds;
}
