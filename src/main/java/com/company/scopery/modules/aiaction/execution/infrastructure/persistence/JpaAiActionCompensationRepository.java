package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionCompensation;
import com.company.scopery.modules.aiaction.execution.infrastructure.mapper.AiActionCompensationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionCompensationRepository {

    private final SpringDataAiActionCompensationJpaRepository springDataRepository;
    private final AiActionCompensationPersistenceMapper mapper;

    public JpaAiActionCompensationRepository(SpringDataAiActionCompensationJpaRepository springDataRepository,
                                              AiActionCompensationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    public AiActionCompensation save(AiActionCompensation compensation) {
        AiActionCompensationJpaEntity entity = mapper.toJpaEntity(compensation);
        AiActionCompensationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    public Optional<AiActionCompensation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    public List<AiActionCompensation> findByExecutionId(UUID executionId) {
        return springDataRepository.findByExecutionId(executionId)
                .stream().map(mapper::toDomain).toList();
    }
}
