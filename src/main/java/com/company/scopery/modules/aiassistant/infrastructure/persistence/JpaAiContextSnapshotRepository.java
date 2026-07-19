package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiContextSnapshot;
import com.company.scopery.modules.aiassistant.domain.model.AiContextSnapshotRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiContextSnapshotPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiContextSnapshotRepository implements AiContextSnapshotRepository {

    private final SpringDataAiContextSnapshotJpaRepository springDataRepository;
    private final AiContextSnapshotPersistenceMapper mapper;

    public JpaAiContextSnapshotRepository(SpringDataAiContextSnapshotJpaRepository springDataRepository,
                                          AiContextSnapshotPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiContextSnapshot save(AiContextSnapshot snapshot) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(snapshot)));
    }

    @Override
    public Optional<AiContextSnapshot> findByAssistantMessageId(UUID assistantMessageId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("assistantMessageId"), assistantMessageId)
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public Optional<AiContextSnapshot> findByConversationIdAndTurnId(UUID conversationId, UUID turnId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("conversationId"), conversationId),
                        cb.equal(root.get("turnId"), turnId)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public List<AiContextSnapshot> findExpiredBefore(Instant threshold) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.lessThan(root.get("expiresAt"), threshold)
        ).stream().map(mapper::toDomain).toList();
    }
}
