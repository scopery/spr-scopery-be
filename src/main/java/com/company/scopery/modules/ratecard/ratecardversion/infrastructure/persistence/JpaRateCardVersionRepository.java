package com.company.scopery.modules.ratecard.ratecardversion.infrastructure.persistence;

import com.company.scopery.modules.ratecard.ratecardversion.domain.enums.RateCardVersionStatus;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersion;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.ratecardversion.infrastructure.mapper.RateCardVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRateCardVersionRepository implements RateCardVersionRepository {
    private final SpringDataRateCardVersionJpaRepository springDataRepository;
    private final RateCardVersionPersistenceMapper mapper;

    public JpaRateCardVersionRepository(SpringDataRateCardVersionJpaRepository springDataRepository,
                                        RateCardVersionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override public RateCardVersion save(RateCardVersion version) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(version)));
    }
    @Override public Optional<RateCardVersion> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
    @Override public List<RateCardVersion> findByRateCardId(UUID rateCardId) {
        return springDataRepository.findAllByRateCardIdOrderByVersionNumberAsc(rateCardId).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<Integer> findMaxVersionNumber(UUID rateCardId) {
        return springDataRepository.findMaxVersionNumber(rateCardId);
    }
    @Override public List<RateCardVersion> findPublishedByRateCardId(UUID rateCardId) {
        return springDataRepository.findAllByRateCardIdAndStatus(rateCardId, RateCardVersionStatus.PUBLISHED.name())
                .stream().map(mapper::toDomain).toList();
    }
    @Override public List<RateCardVersion> findPublishedCoveringDate(UUID rateCardId, LocalDate targetDate) {
        return springDataRepository.findPublishedCoveringDate(rateCardId, targetDate).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsPublishedOverlap(UUID rateCardId, LocalDate from, LocalDate to, UUID excludeVersionId) {
        for (RateCardVersion published : findPublishedByRateCardId(rateCardId)) {
            if (excludeVersionId != null && excludeVersionId.equals(published.id())) continue;
            LocalDate pFrom = published.effectiveFrom();
            LocalDate pTo = published.effectiveTo();
            boolean overlaps = !from.isAfter(pTo == null ? LocalDate.MAX : pTo)
                    && !(to != null && to.isBefore(pFrom));
            if (overlaps) return true;
        }
        return false;
    }
}
