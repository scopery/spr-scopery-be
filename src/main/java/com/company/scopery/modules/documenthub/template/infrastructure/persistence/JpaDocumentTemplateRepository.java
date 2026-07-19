package com.company.scopery.modules.documenthub.template.infrastructure.persistence;
import com.company.scopery.modules.documenthub.template.domain.model.*;
import com.company.scopery.modules.documenthub.template.infrastructure.mapper.DocumentTemplatePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDocumentTemplateRepository implements DocumentTemplateRepository {
    private final SpringDataDocumentTemplateJpaRepository springData;
    private final DocumentTemplatePersistenceMapper mapper;
    public JpaDocumentTemplateRepository(SpringDataDocumentTemplateJpaRepository springData, DocumentTemplatePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DocumentTemplate save(DocumentTemplate e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DocumentTemplate> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }
    @Override public List<DocumentTemplate> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
