package com.company.scopery.modules.quote.quote.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuoteRepository {
    Quote save(Quote quote);
    Optional<Quote> findById(UUID id);
    Optional<Quote> findByIdAndProjectId(UUID id, UUID projectId);
    List<Quote> findByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}
