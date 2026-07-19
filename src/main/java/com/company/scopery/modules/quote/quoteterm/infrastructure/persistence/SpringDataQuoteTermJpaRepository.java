package com.company.scopery.modules.quote.quoteterm.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataQuoteTermJpaRepository extends JpaRepository<QuoteTermJpaEntity, UUID> {
    Optional<QuoteTermJpaEntity> findByIdAndQuoteVersionId(UUID id, UUID quoteVersionId);
    List<QuoteTermJpaEntity> findByQuoteVersionIdOrderByDisplayOrderAsc(UUID quoteVersionId);
}
