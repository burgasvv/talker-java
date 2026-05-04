package org.burgas.talkerjava.service.document;

import org.burgas.talkerjava.dto.Request;

public interface ModifyImage<R extends Request> {

    void makePreview(R request);
}
