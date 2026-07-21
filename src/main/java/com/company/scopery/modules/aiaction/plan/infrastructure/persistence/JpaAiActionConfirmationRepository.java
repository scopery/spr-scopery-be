package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmation;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmationRepository;
import com.company.scopery.modules.aiaction.plan.infrastructure.mapper.AiActionConfirmationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionConfirmationRepository implements AiActionConfirmationRepository {

    private final SpringDataAiActionConfirmationJpaRepository springDataRepository;
    private final AiActionConfirmationPersistenceMapper mapper;

    public JpaAiActionConfirmationRepository(SpringDataAiActionConfirmationJpaRepository springDataRepository,
                                              AiActionConfirmationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionConfirmation save(AiActionConfirmation confirmation) {
        AiActionConfirmationJpaEntity entity = mapper.toJpaEntity(confirmation);
        AiActionConfirmationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionConfirmation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiActionConfirmation> findLatestByPlanId(UUID planId) {
        return springDataRepository.findLatestByPlanId(planId).map(mapper::toDomain);
    }
}
