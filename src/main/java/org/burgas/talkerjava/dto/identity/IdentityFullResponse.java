package org.burgas.talkerjava.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.identity.IdentityImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.chat.ChatShortResponse;
import org.burgas.talkerjava.dto.community.CommunityShortResponse;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityFullResponse implements Response {

    private UUID id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String patronymic;
    private Set<IdentityImage> images;
    private Set<ChatShortResponse> chats;
    private Set<CommunityShortResponse> communities;
}
