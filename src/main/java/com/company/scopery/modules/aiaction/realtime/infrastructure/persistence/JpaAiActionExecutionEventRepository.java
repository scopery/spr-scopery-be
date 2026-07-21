package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEventRepository;
import com.company.scopery.modules.aiaction.realtime.infrastructure.mapper.AiActionExecutionEventPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionExecutionEventRepository implements AiActionExecutionEventRepository {

    private final SpringDataAiActionExecutionEventJpaRepository springDataRepository;
    private final AiActionExecutionEventPersistenceMapper mapper;

    public JpaAiActionExecutionEventRepository(SpringDataAiActionExecutionEventJpaRepository springDataRepository,
                                                AiActionExecutionEventPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionExecutionEvent save(AiActionExecutionEvent event) {
        AiActionExecutionEventJpaEntity entity = mapper.toJpaEntity(event);
        AiActionExecutionEventJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public long nextSequenceForExecution(UUID executionId) {
        return springDataRepository.findMaxSequenceByExecutionId(executionId) + 1;
    }

    @Override
    public List<AiActionExecutionEvent> findByExecutionIdAndSequenceGreaterThan(UUID executionId,
                                                                                  long afterSequence, int limit) {
        return springDataRepository.findByExecutionIdAndSequenceGreaterThan(executionId, afterSequence)
                .stream().limit(limit).map(mapper::toDomain).toList();
    }

    @Override
    public List<AiActionExecutionEvent> findUnpublishedSince(Instant since, int limit) {
        return springDataRepository.findUnpublishedSince(since)
                .stream().limit(limit).map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Long> findMinRetainedSequence(UUID executionId) {
        return springDataRepository.findMinSequenceByExecutionId(executionId);
    }
}
