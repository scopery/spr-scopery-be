package com.company.scopery.modules.quote.quote.infrastructure.mapper;

import com.company.scopery.modules.quote.quote.domain.enums.QuoteStatus;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.infrastructure.persistence.QuoteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class QuotePersistenceMapper {
    public Quote toDomain(QuoteJpaEntity e) {
        return new Quote(
                e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceFinanceScenarioId(),
                e.getCode(), e.getTitle(), e.getDescription(), e.getClientName(), e.getClientCompany(),
                e.getClientEmail(), e.getClientContactName(), e.getClientReference(), e.getExternalPartyId(),
                QuoteStatus.valueOf(e.getStatus()), e.getCurrentVersionId(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public QuoteJpaEntity toJpaEntity(Quote d) {
        QuoteJpaEntity e = new QuoteJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setSourceFinanceScenarioId(d.sourceFinanceScenarioId());
        e.setCode(d.code());
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setClientName(d.clientName());
        e.setClientCompany(d.clientCompany());
        e.setClientEmail(d.clientEmail());
        e.setClientContactName(d.clientContactName());
        e.setClientReference(d.clientReference());
        e.setExternalPartyId(d.externalPartyId());
        e.setStatus(d.status().name());
        e.setCurrentVersionId(d.currentVersionId());
        e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
