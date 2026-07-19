package com.company.scopery.modules.quote.quote.infrastructure.persistence;

import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.quote.infrastructure.mapper.QuotePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaQuoteRepository implements QuoteRepository {
    private final SpringDataQuoteJpaRepository springData;
    private final QuotePersistenceMapper mapper;

    public JpaQuoteRepository(SpringDataQuoteJpaRepository springData, QuotePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Quote save(Quote quote) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(quote)));
    }

    @Override
    public Optional<Quote> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Quote> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<Quote> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springData.existsByProjectIdAndCode(projectId, code);
    }
}
