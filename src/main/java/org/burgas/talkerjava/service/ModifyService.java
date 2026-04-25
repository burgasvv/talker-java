package org.burgas.talkerjava.service;

import org.burgas.talkerjava.dto.Request;
import org.burgas.talkerjava.dto.Response;

public interface ModifyService<R extends Request, F extends Response> {

    F update(R request);
}
