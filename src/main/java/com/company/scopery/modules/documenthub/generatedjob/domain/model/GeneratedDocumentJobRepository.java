package com.company.scopery.modules.documenthub.generatedjob.domain.model;
import java.util.*;
public interface GeneratedDocumentJobRepository {
    GeneratedDocumentJob save(GeneratedDocumentJob entity);
    Optional<GeneratedDocumentJob> findByIdAndProjectId(UUID id, UUID projectId);
    List<GeneratedDocumentJob> findByProjectId(UUID projectId);
}
