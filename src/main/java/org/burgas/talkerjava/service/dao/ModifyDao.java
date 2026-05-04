package org.burgas.talkerjava.service.dao;

import org.burgas.talkerjava.dto.Request;
import org.burgas.talkerjava.dto.Response;

public interface ModifyDao<R extends Request, F extends Response> {

    F update(R request);
}
