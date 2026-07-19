package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.util.List;
import java.util.UUID;

public interface DocumentRelationRepository {
    void saveAll(List<DocumentRelation> relations);
    void deleteBySourceDocumentId(UUID sourceDocumentId);
    List<DocumentRelation> findBySourceDocumentId(UUID sourceDocumentId);
    List<DocumentRelation> findByTargetDocumentId(UUID targetDocumentId);
}
