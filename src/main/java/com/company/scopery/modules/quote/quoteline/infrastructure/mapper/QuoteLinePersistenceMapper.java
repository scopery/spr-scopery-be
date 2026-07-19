package com.company.scopery.modules.quote.quoteline.infrastructure.mapper;

import com.company.scopery.modules.quote.quoteline.domain.enums.QuoteLineType;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.infrastructure.persistence.QuoteLineJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class QuoteLinePersistenceMapper {
    public QuoteLine toDomain(QuoteLineJpaEntity e) {
        return new QuoteLine(
                e.getId(), e.getQuoteVersionId(), e.getProjectId(), e.getSourcePhaseFinanceId(),
                e.getSourceProjectPhaseId(), QuoteLineType.valueOf(e.getLineType()), e.getName(),
                e.getDescription(), e.getQuantity(), e.getUnitPrice(), e.getAmount(), e.getCurrencyCode(),
                e.getDisplayOrder() == null ? 0 : e.getDisplayOrder(),
                Boolean.TRUE.equals(e.getClientVisible()), e.getInternalNote(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public QuoteLineJpaEntity toJpaEntity(QuoteLine d) {
        QuoteLineJpaEntity e = new QuoteLineJpaEntity();
        e.setId(d.id());
        e.setQuoteVersionId(d.quoteVersionId());
        e.setProjectId(d.projectId());
        e.setSourcePhaseFinanceId(d.sourcePhaseFinanceId());
        e.setSourceProjectPhaseId(d.sourceProjectPhaseId());
        e.setLineType(d.lineType().name());
        e.setName(d.name());
        e.setDescription(d.description());
        e.setQuantity(d.quantity());
        e.setUnitPrice(d.unitPrice());
        e.setAmount(d.amount());
        e.setCurrencyCode(d.currencyCode());
        e.setDisplayOrder(d.displayOrder());
        e.setClientVisible(d.clientVisible());
        e.setInternalNote(d.internalNote());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
