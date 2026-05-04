package org.burgas.talkerjava.service.document;

import org.burgas.talkerjava.dto.document.DocumentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DesignDocument<ID> {

    void create(ID entityId, List<MultipartFile> multipartFiles);

    void delete(ID entityId, DocumentRequest documentRequest);
}
