package org.burgas.talkerjava.cache;

import org.burgas.talkerjava.dao.Dao;

public interface RedisCacheHandler<D extends Dao> {

    void handleCache(D entity);
}
