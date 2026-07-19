package com.company.scopery.modules.documenthub.share.infrastructure.persistence;
import com.company.scopery.modules.documenthub.share.domain.model.*;
import com.company.scopery.modules.documenthub.share.infrastructure.mapper.DocumentSharePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDocumentShareRepository implements DocumentShareRepository {
    private final SpringDataDocumentShareJpaRepository springData;
    private final DocumentSharePersistenceMapper mapper;
    public JpaDocumentShareRepository(SpringDataDocumentShareJpaRepository springData, DocumentSharePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DocumentShare save(DocumentShare e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DocumentShare> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DocumentShare> findByProjectIdAndDocumentId(UUID projectId, UUID documentId) {
        return springData.findByProjectIdAndDocumentIdOrderByCreatedAtDesc(projectId, documentId).stream().map(mapper::toDomain).toList();
    }
}
