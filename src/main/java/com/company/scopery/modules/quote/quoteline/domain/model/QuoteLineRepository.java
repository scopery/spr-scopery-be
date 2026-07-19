package com.company.scopery.modules.quote.quoteline.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteLineRepository {
    QuoteLine save(QuoteLine line);
    Optional<QuoteLine> findById(UUID id);
    Optional<QuoteLine> findByIdAndVersionId(UUID id, UUID quoteVersionId);
    List<QuoteLine> findByQuoteVersionId(UUID quoteVersionId);
    void delete(QuoteLine line);
    void deleteByQuoteVersionId(UUID quoteVersionId);
}
