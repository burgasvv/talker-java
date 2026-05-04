package org.burgas.talkerjava.service.document;

import org.burgas.talkerjava.dao.Document;

public interface ReadDocument<ID, D extends Document> {

    D findEntity(ID id);
}
