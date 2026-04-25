package org.burgas.talkerjava.mapper.contract;

import jakarta.servlet.http.Part;
import org.burgas.talkerjava.dao.Dao;
import org.burgas.talkerjava.dao.Document;

public interface Uploader<D extends Dao, E extends Document> {

    E upload(D entity, Part part);
}
