package com.company.scopery.modules.quote.quotesummary.infrastructure.persistence;

import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quotesummary.infrastructure.mapper.QuoteSummaryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaQuoteSummaryRepository implements QuoteSummaryRepository {
    private final SpringDataQuoteSummaryJpaRepository springData;
    private final QuoteSummaryPersistenceMapper mapper;

    public JpaQuoteSummaryRepository(SpringDataQuoteSummaryJpaRepository springData,
                                     QuoteSummaryPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public QuoteSummary save(QuoteSummary summary) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(summary)));
    }

    @Override
    public Optional<QuoteSummary> findByQuoteVersionId(UUID quoteVersionId) {
        return springData.findByQuoteVersionId(quoteVersionId).map(mapper::toDomain);
    }
}
