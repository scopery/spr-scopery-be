package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiToolCallRecord;
import com.company.scopery.modules.aiassistant.domain.model.AiToolCallRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiToolCallPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiToolCallRepository implements AiToolCallRepository {

    private final SpringDataAiToolCallJpaRepository springDataRepository;
    private final AiToolCallPersistenceMapper mapper;

    public JpaAiToolCallRepository(SpringDataAiToolCallJpaRepository springDataRepository,
                                   AiToolCallPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiToolCallRecord save(AiToolCallRecord record) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(record)));
    }

    @Override
    public Optional<AiToolCallRecord> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiToolCallRecord> findByRequestMessageId(UUID requestMessageId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("requestMessageId"), requestMessageId)
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public List<AiToolCallRecord> findByConversationIdAndTurnId(UUID conversationId, UUID turnId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("conversationId"), conversationId),
                        cb.equal(root.get("turnId"), turnId)
                )
        ).stream().map(mapper::toDomain).toList();
    }
}
