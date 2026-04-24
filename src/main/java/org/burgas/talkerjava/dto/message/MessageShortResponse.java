package org.burgas.talkerjava.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.message.MessageFile;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageShortResponse implements Response {

    private UUID id;
    private IdentityShortResponse sender;
    private String text;
    private List<MessageFile> files;
    private String createdAt;
}
