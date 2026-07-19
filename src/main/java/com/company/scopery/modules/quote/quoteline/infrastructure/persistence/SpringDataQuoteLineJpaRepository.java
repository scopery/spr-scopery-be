package com.company.scopery.modules.quote.quoteline.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataQuoteLineJpaRepository extends JpaRepository<QuoteLineJpaEntity, UUID> {
    Optional<QuoteLineJpaEntity> findByIdAndQuoteVersionId(UUID id, UUID quoteVersionId);
    List<QuoteLineJpaEntity> findByQuoteVersionIdOrderByDisplayOrderAsc(UUID quoteVersionId);
    void deleteByQuoteVersionId(UUID quoteVersionId);
}
