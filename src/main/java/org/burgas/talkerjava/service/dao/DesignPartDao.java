package org.burgas.talkerjava.service.dao;

import org.burgas.talkerjava.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignPartDao<ID, F extends Response> {

    F create(MultipartFile request, List<MultipartFile> files);

    void delete(ID id);
}
