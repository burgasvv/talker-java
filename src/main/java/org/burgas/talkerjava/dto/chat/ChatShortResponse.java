package org.burgas.talkerjava.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.chat.ChatImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatShortResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private IdentityShortResponse admin;
    private Set<ChatImage> images;
    private String createdAt;
}
