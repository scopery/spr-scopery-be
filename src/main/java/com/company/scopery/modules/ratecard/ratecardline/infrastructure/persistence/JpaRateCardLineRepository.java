package com.company.scopery.modules.ratecard.ratecardline.infrastructure.persistence;

import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLine;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardline.infrastructure.mapper.RateCardLinePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;

@Repository
public class JpaRateCardLineRepository implements RateCardLineRepository {
    private final SpringDataRateCardLineJpaRepository springDataRepository;
    private final RateCardLinePersistenceMapper mapper;

    public JpaRateCardLineRepository(SpringDataRateCardLineJpaRepository springDataRepository,
                                     RateCardLinePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository; this.mapper = mapper;
    }

    @Override public RateCardLine save(RateCardLine line) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(line)));
    }
    @Override public Optional<RateCardLine> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
    @Override public List<RateCardLine> findByVersionId(UUID versionId) {
        return springDataRepository.findAllByRateCardVersionId(versionId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsDuplicate(UUID versionId, UUID costRoleId, String seniorityLevel,
                                             String locationCode, String currencyCode, UUID excludeLineId) {
        return springDataRepository.existsDuplicate(versionId, costRoleId, seniorityLevel, locationCode, currencyCode, excludeLineId);
    }
    @Override public void delete(UUID id) { springDataRepository.deleteById(id); }
}
