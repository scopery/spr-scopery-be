package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.execution.infrastructure.mapper.AiActionExecutionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionExecutionRepository implements AiActionExecutionRepository {

    private final SpringDataAiActionExecutionJpaRepository springDataRepository;
    private final AiActionExecutionPersistenceMapper mapper;

    public JpaAiActionExecutionRepository(SpringDataAiActionExecutionJpaRepository springDataRepository,
                                           AiActionExecutionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionExecution save(AiActionExecution execution) {
        AiActionExecutionJpaEntity entity = mapper.toJpaEntity(execution);
        AiActionExecutionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionExecution> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiActionExecution> findByPlanId(UUID planId) {
        return springDataRepository.findByPlanId(planId).map(mapper::toDomain);
    }

    @Override
    public Optional<AiActionExecution> findByExecutionKey(String executionKey) {
        return springDataRepository.findByExecutionKey(executionKey).map(mapper::toDomain);
    }

    @Override
    public int countActiveByUserAndWorkspace(UUID userId, UUID workspaceId,
                                              List<AiActionExecutionStatus> activeStatuses) {
        List<String> statusNames = activeStatuses.stream().map(Enum::name).toList();
        return springDataRepository.countByInitiatedByUserIdAndStatusIn(userId, statusNames);
    }

    @Override
    public List<AiActionExecution> findQueuedOrExpiredLeaseForClaim(int limit) {
        return springDataRepository.findClaimable(limit)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<AiActionExecution> findAndLockById(UUID id) {
        return springDataRepository.findAndLockById(id).map(mapper::toDomain);
    }
}
