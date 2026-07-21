package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionControlCommand;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionControlCommandRepository;
import com.company.scopery.modules.aiaction.realtime.infrastructure.mapper.AiActionControlCommandPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionControlCommandRepository implements AiActionControlCommandRepository {

    private final SpringDataAiActionControlCommandJpaRepository springDataRepository;
    private final AiActionControlCommandPersistenceMapper mapper;

    public JpaAiActionControlCommandRepository(SpringDataAiActionControlCommandJpaRepository springDataRepository,
                                                AiActionControlCommandPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionControlCommand save(AiActionControlCommand command) {
        AiActionControlCommandJpaEntity entity = mapper.toJpaEntity(command);
        AiActionControlCommandJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionControlCommand> findByIdempotencyKey(String idempotencyKey) {
        return springDataRepository.findByIdempotencyKey(idempotencyKey)
                .map(mapper::toDomain);
    }

    @Override
    public List<AiActionControlCommand> findPendingByExecutionId(UUID executionId) {
        return springDataRepository.findByExecutionIdAndStatusOrderByCreatedAtAsc(executionId, "PENDING")
                .stream().map(mapper::toDomain).toList();
    }
}
