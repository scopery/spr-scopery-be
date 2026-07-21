package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecutionRepository;
import com.company.scopery.modules.aiaction.execution.infrastructure.mapper.AiActionStepExecutionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionStepExecutionRepository implements AiActionStepExecutionRepository {

    private final SpringDataAiActionStepExecutionJpaRepository springDataRepository;
    private final AiActionStepExecutionPersistenceMapper mapper;

    public JpaAiActionStepExecutionRepository(SpringDataAiActionStepExecutionJpaRepository springDataRepository,
                                               AiActionStepExecutionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionStepExecution save(AiActionStepExecution stepExecution) {
        AiActionStepExecutionJpaEntity entity = mapper.toJpaEntity(stepExecution);
        AiActionStepExecutionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionStepExecution> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AiActionStepExecution> findByExecutionIdOrderByOrdinal(UUID executionId) {
        return springDataRepository.findByExecutionIdOrderByOrdinal(executionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AiActionStepExecution> findByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.findByIdempotencyKey(idempotencyKey).map(mapper::toDomain);
    }

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.existsByIdempotencyKey(idempotencyKey);
    }
}
