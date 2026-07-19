package com.company.scopery.modules.quote.quoteline.infrastructure.persistence;

import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteline.infrastructure.mapper.QuoteLinePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaQuoteLineRepository implements QuoteLineRepository {
    private final SpringDataQuoteLineJpaRepository springData;
    private final QuoteLinePersistenceMapper mapper;

    public JpaQuoteLineRepository(SpringDataQuoteLineJpaRepository springData, QuoteLinePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public QuoteLine save(QuoteLine line) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(line)));
    }

    @Override
    public Optional<QuoteLine> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<QuoteLine> findByIdAndVersionId(UUID id, UUID quoteVersionId) {
        return springData.findByIdAndQuoteVersionId(id, quoteVersionId).map(mapper::toDomain);
    }

    @Override
    public List<QuoteLine> findByQuoteVersionId(UUID quoteVersionId) {
        return springData.findByQuoteVersionIdOrderByDisplayOrderAsc(quoteVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(QuoteLine line) {
        springData.deleteById(line.id());
    }

    @Override
    public void deleteByQuoteVersionId(UUID quoteVersionId) {
        springData.deleteByQuoteVersionId(quoteVersionId);
    }
}
