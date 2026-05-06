package org.burgas.talkerjava.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.chat.ChatImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.dto.message.MessageShortResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatFullResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private IdentityShortResponse admin;
    private Set<ChatImage> images;
    private Set<IdentityShortResponse> identities;
    private Set<MessageShortResponse> messages;
    private String createdAt;
}
