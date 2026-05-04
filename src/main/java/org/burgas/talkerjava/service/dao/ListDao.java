package org.burgas.talkerjava.service.dao;

import org.burgas.talkerjava.dto.Response;

import java.util.List;

public interface ListDao<S extends Response> {

    List<S> findAll();
}
