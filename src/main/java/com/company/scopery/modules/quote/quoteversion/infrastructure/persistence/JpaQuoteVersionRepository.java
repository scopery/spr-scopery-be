package com.company.scopery.modules.quote.quoteversion.infrastructure.persistence;

import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.quoteversion.infrastructure.mapper.QuoteVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaQuoteVersionRepository implements QuoteVersionRepository {
    private final SpringDataQuoteVersionJpaRepository springData;
    private final QuoteVersionPersistenceMapper mapper;

    public JpaQuoteVersionRepository(SpringDataQuoteVersionJpaRepository springData,
                                     QuoteVersionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public QuoteVersion save(QuoteVersion version) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(version)));
    }

    @Override
    public Optional<QuoteVersion> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<QuoteVersion> findByIdAndQuoteId(UUID id, UUID quoteId) {
        return springData.findByIdAndQuoteId(id, quoteId).map(mapper::toDomain);
    }

    @Override
    public List<QuoteVersion> findByQuoteId(UUID quoteId) {
        return springData.findByQuoteIdOrderByVersionNumberDesc(quoteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<QuoteVersion> findCurrentFlagged(UUID quoteId) {
        return springData.findByQuoteIdAndCurrentFlagTrue(quoteId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int nextVersionNumber(UUID quoteId) {
        return springData.findMaxVersionNumber(quoteId) + 1;
    }
}
