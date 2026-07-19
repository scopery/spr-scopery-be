package com.company.scopery.modules.quote.quotesummary.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface QuoteSummaryRepository {
    QuoteSummary save(QuoteSummary summary);
    Optional<QuoteSummary> findByQuoteVersionId(UUID quoteVersionId);
}
