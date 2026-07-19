package com.company.scopery.modules.quote.quoteterm.infrastructure.mapper;

import com.company.scopery.modules.quote.quoteterm.domain.enums.QuoteTermType;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.infrastructure.persistence.QuoteTermJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class QuoteTermPersistenceMapper {
    public QuoteTerm toDomain(QuoteTermJpaEntity e) {
        return new QuoteTerm(
                e.getId(), e.getQuoteVersionId(), e.getProjectId(),
                QuoteTermType.valueOf(e.getTermType()), e.getTitle(), e.getContent(),
                e.getDisplayOrder() == null ? 0 : e.getDisplayOrder(),
                Boolean.TRUE.equals(e.getClientVisible()),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public QuoteTermJpaEntity toJpaEntity(QuoteTerm d) {
        QuoteTermJpaEntity e = new QuoteTermJpaEntity();
        e.setId(d.id());
        e.setQuoteVersionId(d.quoteVersionId());
        e.setProjectId(d.projectId());
        e.setTermType(d.termType().name());
        e.setTitle(d.title());
        e.setContent(d.content());
        e.setDisplayOrder(d.displayOrder());
        e.setClientVisible(d.clientVisible());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
