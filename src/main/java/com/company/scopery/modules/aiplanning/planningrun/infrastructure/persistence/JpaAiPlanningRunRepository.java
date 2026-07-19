package com.company.scopery.modules.aiplanning.planningrun.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRunRepository;
import com.company.scopery.modules.aiplanning.planningrun.infrastructure.mapper.AiPlanningRunPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiPlanningRunRepository implements AiPlanningRunRepository {
    private final SpringDataAiPlanningRunJpaRepository springData;
    private final AiPlanningRunPersistenceMapper mapper;

    public JpaAiPlanningRunRepository(SpringDataAiPlanningRunJpaRepository springData,
                                      AiPlanningRunPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public AiPlanningRun save(AiPlanningRun run) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(run)));
    }

    @Override
    public Optional<AiPlanningRun> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiPlanningRun> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public List<AiPlanningRun> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
