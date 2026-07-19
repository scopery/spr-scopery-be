package com.company.scopery.modules.quote.quoteversion.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteVersionRepository {
    QuoteVersion save(QuoteVersion version);
    Optional<QuoteVersion> findById(UUID id);
    Optional<QuoteVersion> findByIdAndQuoteId(UUID id, UUID quoteId);
    List<QuoteVersion> findByQuoteId(UUID quoteId);
    List<QuoteVersion> findCurrentFlagged(UUID quoteId);
    int nextVersionNumber(UUID quoteId);
}
