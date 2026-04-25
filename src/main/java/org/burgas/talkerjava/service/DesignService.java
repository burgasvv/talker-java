package org.burgas.talkerjava.service;

import org.burgas.talkerjava.dto.Request;
import org.burgas.talkerjava.dto.Response;

public interface DesignService<ID, R extends Request, F extends Response> {

    F create(R request);

    void delete(ID id);
}
