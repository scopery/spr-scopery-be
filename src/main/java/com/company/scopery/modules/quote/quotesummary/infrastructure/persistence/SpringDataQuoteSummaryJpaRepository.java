package com.company.scopery.modules.quote.quotesummary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataQuoteSummaryJpaRepository extends JpaRepository<QuoteSummaryJpaEntity, UUID> {
    Optional<QuoteSummaryJpaEntity> findByQuoteVersionId(UUID quoteVersionId);
}
