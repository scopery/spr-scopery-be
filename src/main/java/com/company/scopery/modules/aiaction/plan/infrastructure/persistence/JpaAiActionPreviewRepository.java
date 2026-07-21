package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreview;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPreviewRepository;
import com.company.scopery.modules.aiaction.plan.infrastructure.mapper.AiActionPreviewPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionPreviewRepository implements AiActionPreviewRepository {

    private final SpringDataAiActionPreviewJpaRepository springDataRepository;
    private final AiActionPreviewPersistenceMapper mapper;

    public JpaAiActionPreviewRepository(SpringDataAiActionPreviewJpaRepository springDataRepository,
                                         AiActionPreviewPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionPreview save(AiActionPreview preview) {
        AiActionPreviewJpaEntity entity = mapper.toJpaEntity(preview);
        AiActionPreviewJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionPreview> findByPlanId(UUID planId) {
        return springDataRepository.findByPlanId(planId).map(mapper::toDomain);
    }
}
