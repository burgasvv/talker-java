package org.burgas.talkerjava.service.document;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignDocument<ID> {

    void create(ID entityId, List<Part> parts);

    void delete(ID entityId, DocumentRequest documentRequest);
}
