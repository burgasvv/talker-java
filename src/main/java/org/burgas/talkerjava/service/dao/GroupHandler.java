package org.burgas.talkerjava.service.dao;

import org.burgas.talkerjava.dto.Request;

public interface GroupHandler<R extends Request> {

    void join(R request);

    void out(R request);
}
