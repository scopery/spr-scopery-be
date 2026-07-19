package com.company.scopery.modules.documenthub.version.infrastructure.persistence;
import com.company.scopery.modules.documenthub.version.domain.model.*;
import com.company.scopery.modules.documenthub.version.infrastructure.mapper.DocumentVersionPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDocumentVersionRepository implements DocumentVersionRepository {
    private final SpringDataDocumentVersionJpaRepository springData;
    private final DocumentVersionPersistenceMapper mapper;
    public JpaDocumentVersionRepository(SpringDataDocumentVersionJpaRepository springData, DocumentVersionPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DocumentVersion save(DocumentVersion e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DocumentVersion> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DocumentVersion> findByProjectIdAndDocumentId(UUID projectId, UUID documentId) {
        return springData.findByProjectIdAndDocumentIdOrderByVersionNumberDesc(projectId, documentId).stream().map(mapper::toDomain).toList();
    }
    @Override public int nextVersionNumber(UUID documentId) { return springData.maxVersionNumber(documentId) + 1; }
}
