package com.company.scopery.modules.documenthub.folder.infrastructure.persistence;
import com.company.scopery.modules.documenthub.folder.domain.model.*;
import com.company.scopery.modules.documenthub.folder.infrastructure.mapper.DocumentFolderPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDocumentFolderRepository implements DocumentFolderRepository {
    private final SpringDataDocumentFolderJpaRepository springData;
    private final DocumentFolderPersistenceMapper mapper;
    public JpaDocumentFolderRepository(SpringDataDocumentFolderJpaRepository springData, DocumentFolderPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DocumentFolder save(DocumentFolder e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DocumentFolder> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DocumentFolder> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderBySortOrderAscCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
