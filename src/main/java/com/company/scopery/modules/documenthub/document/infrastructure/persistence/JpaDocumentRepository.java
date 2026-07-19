package com.company.scopery.modules.documenthub.document.infrastructure.persistence;
import com.company.scopery.modules.documenthub.document.domain.model.*; import com.company.scopery.modules.documenthub.document.infrastructure.mapper.DocumentPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaDocumentRepository implements DocumentRepository {
    private final SpringDataDocumentJpaRepository springData; private final DocumentPersistenceMapper mapper;
    public JpaDocumentRepository(SpringDataDocumentJpaRepository springData, DocumentPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public Document save(Document e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<Document> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<Document> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
}
