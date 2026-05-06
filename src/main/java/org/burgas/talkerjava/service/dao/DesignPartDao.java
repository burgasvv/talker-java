package org.burgas.talkerjava.service.dao;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dto.Request;
import org.burgas.talkerjava.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignPartDao<ID, R extends Request, F extends Response> {

    F create(R request, List<Part> files);

    void delete(ID id);
}
