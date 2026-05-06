package org.burgas.talkerjava.dto.community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.burgas.talkerjava.dao.community.CommunityImage;
import org.burgas.talkerjava.dto.Response;
import org.burgas.talkerjava.dto.identity.IdentityShortResponse;
import org.burgas.talkerjava.dto.publication.PublicationShortResponse;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityFullResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private IdentityShortResponse admin;
    private Set<CommunityImage> images;
    private Set<IdentityShortResponse> identities;
    private Set<PublicationShortResponse> publications;
    private String createdAt;
}
