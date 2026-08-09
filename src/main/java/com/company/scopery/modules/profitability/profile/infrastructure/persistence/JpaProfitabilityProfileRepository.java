package com.company.scopery.modules.profitability.profile.infrastructure.persistence;

import com.company.scopery.modules.profitability.profile.domain.model.ProfitabilityProfile;
import com.company.scopery.modules.profitability.profile.domain.model.ProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.profile.infrastructure.mapper.ProfitabilityProfilePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfitabilityProfileRepository implements ProfitabilityProfileRepository {

    private final SpringDataProfitabilityProfileJpaRepository springData;
    private final ProfitabilityProfilePersistenceMapper mapper;

    public JpaProfitabilityProfileRepository(SpringDataProfitabilityProfileJpaRepository springData,
                                             ProfitabilityProfilePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProfitabilityProfile> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ProfitabilityProfile> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectId(UUID projectId) {
        return springData.existsByProjectId(projectId);
    }

    @Override
    public ProfitabilityProfile save(ProfitabilityProfile profile) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(profile)));
    }
}
