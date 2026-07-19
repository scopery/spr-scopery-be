package com.company.scopery.modules.aicontext.audit.infrastructure.persistence;

import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAudit;
import com.company.scopery.modules.aicontext.audit.domain.model.AiContextResolutionAuditRepository;
import com.company.scopery.modules.aicontext.audit.infrastructure.mapper.AiContextResolutionAuditPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaAiContextResolutionAuditRepository implements AiContextResolutionAuditRepository {

    private final SpringDataAiContextResolutionAuditJpaRepository springDataRepository;
    private final AiContextResolutionAuditPersistenceMapper mapper;

    public JpaAiContextResolutionAuditRepository(
            SpringDataAiContextResolutionAuditJpaRepository springDataRepository,
            AiContextResolutionAuditPersistenceMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiContextResolutionAudit save(AiContextResolutionAudit audit) {
        AiContextResolutionAuditJpaEntity entity = mapper.toJpaEntity(audit);
        // saveAndFlush ensures the entity is flushed to the database immediately.
        AiContextResolutionAuditJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<AiContextResolutionAudit> findByDocumentId(UUID documentId, Pageable pageable) {
        return springDataRepository.findByDocumentId(documentId, pageable)
                .map(mapper::toDomain);
    }
}
