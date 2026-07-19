package com.company.scopery.modules.quote.quoteterm.infrastructure.persistence;

import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
import com.company.scopery.modules.quote.quoteterm.infrastructure.mapper.QuoteTermPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaQuoteTermRepository implements QuoteTermRepository {
    private final SpringDataQuoteTermJpaRepository springData;
    private final QuoteTermPersistenceMapper mapper;

    public JpaQuoteTermRepository(SpringDataQuoteTermJpaRepository springData, QuoteTermPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public QuoteTerm save(QuoteTerm term) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(term)));
    }

    @Override
    public Optional<QuoteTerm> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<QuoteTerm> findByIdAndVersionId(UUID id, UUID quoteVersionId) {
        return springData.findByIdAndQuoteVersionId(id, quoteVersionId).map(mapper::toDomain);
    }

    @Override
    public List<QuoteTerm> findByQuoteVersionId(UUID quoteVersionId) {
        return springData.findByQuoteVersionIdOrderByDisplayOrderAsc(quoteVersionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(QuoteTerm term) {
        springData.deleteById(term.id());
    }
}
