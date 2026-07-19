package com.company.scopery.modules.quote.quoteterm.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteTermRepository {
    QuoteTerm save(QuoteTerm term);
    Optional<QuoteTerm> findById(UUID id);
    Optional<QuoteTerm> findByIdAndVersionId(UUID id, UUID quoteVersionId);
    List<QuoteTerm> findByQuoteVersionId(UUID quoteVersionId);
    void delete(QuoteTerm term);
}
