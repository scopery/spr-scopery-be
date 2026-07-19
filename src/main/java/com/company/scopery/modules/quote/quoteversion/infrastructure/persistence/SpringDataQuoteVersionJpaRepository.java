package com.company.scopery.modules.quote.quoteversion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataQuoteVersionJpaRepository extends JpaRepository<QuoteVersionJpaEntity, UUID> {
    Optional<QuoteVersionJpaEntity> findByIdAndQuoteId(UUID id, UUID quoteId);
    List<QuoteVersionJpaEntity> findByQuoteIdOrderByVersionNumberDesc(UUID quoteId);
    List<QuoteVersionJpaEntity> findByQuoteIdAndCurrentFlagTrue(UUID quoteId);

    @Query("select coalesce(max(v.versionNumber), 0) from QuoteVersionJpaEntity v where v.quoteId = :quoteId")
    int findMaxVersionNumber(@Param("quoteId") UUID quoteId);
}
