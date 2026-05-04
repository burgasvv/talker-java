package org.burgas.talkerjava.service.dao;

import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dto.Response;

public interface ReadDao<ID, D extends Dao, F extends Response> {

    D findEntity(ID id);

    F findById(ID id);
}
