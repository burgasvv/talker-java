package org.burgas.talkerjava.dto.group;

import lombok.*;
import org.burgas.talkerjava.dto.Request;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest implements Request {

    private UUID groupId;
    private UUID applicantId;
}
