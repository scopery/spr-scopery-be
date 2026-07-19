package com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshot;
import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshotRepository;
import com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.mapper.AiPlanningContextSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiPlanningContextSnapshotRepository implements AiPlanningContextSnapshotRepository {
    private final SpringDataAiPlanningContextSnapshotJpaRepository springData;
    private final AiPlanningContextSnapshotPersistenceMapper mapper;

    public JpaAiPlanningContextSnapshotRepository(SpringDataAiPlanningContextSnapshotJpaRepository springData,
                                                  AiPlanningContextSnapshotPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public AiPlanningContextSnapshot save(AiPlanningContextSnapshot snapshot) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(snapshot)));
    }

    @Override
    public Optional<AiPlanningContextSnapshot> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
}
