package org.burgas.talkerjava.mapper.contract;

import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dto.Request;
import org.burgas.talkerjava.dto.Response;

public interface Mapper<R extends Request, D extends Dao, S extends Response, F extends Response> {

    D toEntity(R request);

    S toShortResponse(D entity);

    F toFullResponse(D entity);

    default <E> E handleData(E requestData, E entityData) {
        return requestData != null ? requestData : entityData;
    }

    default <E> E handleDataException(E data, String message) {
        if (data != null) {
            return data;
        } else {
            throw new IllegalArgumentException(message);
        }
    }
}
