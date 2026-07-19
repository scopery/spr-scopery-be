package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationRunRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiRecommendationRunPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiRecommendationRunRepository implements AiRecommendationRunRepository {

    private final SpringDataAiRecommendationRunJpaRepository springDataRepository;
    private final AiRecommendationRunPersistenceMapper mapper;

    public JpaAiRecommendationRunRepository(
            SpringDataAiRecommendationRunJpaRepository springDataRepository,
            AiRecommendationRunPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiRecommendationRun save(AiRecommendationRun run) {
        AiRecommendationRunJpaEntity entity = mapper.toJpaEntity(run);
        AiRecommendationRunJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiRecommendationRun> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiRecommendationRun> findByWorkspaceProjectAndIdempotencyKey(
            UUID workspaceId, UUID projectId, String idempotencyKey) {
        return springDataRepository
                .findByWorkspaceIdAndProjectIdAndIdempotencyKey(workspaceId, projectId, idempotencyKey)
                .map(mapper::toDomain);
    }
}
