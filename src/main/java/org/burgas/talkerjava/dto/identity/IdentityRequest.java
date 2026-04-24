package org.burgas.talkerjava.dto.identity;

import lombok.*;
import org.burgas.talkerjava.dao.identity.Authority;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityRequest implements Request {

    private UUID id;
    private Authority authority;
    private String username;
    private String password;
    private String email;
    private Boolean status;
    private String firstname;
    private String lastname;
    private String patronymic;
}
