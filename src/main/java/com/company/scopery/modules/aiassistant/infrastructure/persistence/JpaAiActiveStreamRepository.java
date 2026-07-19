package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.enums.ActiveStreamStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiActiveStream;
import com.company.scopery.modules.aiassistant.domain.model.AiActiveStreamRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiActiveStreamPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActiveStreamRepository implements AiActiveStreamRepository {

    private final SpringDataAiActiveStreamJpaRepository springDataRepository;
    private final AiActiveStreamPersistenceMapper mapper;

    public JpaAiActiveStreamRepository(SpringDataAiActiveStreamJpaRepository springDataRepository,
                                       AiActiveStreamPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActiveStream save(AiActiveStream stream) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(stream)));
    }

    @Override
    public Optional<AiActiveStream> findByMessageId(UUID messageId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("messageId"), messageId)
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public int countByWorkspaceIdAndActorIdAndStreamStatus(UUID workspaceId, UUID actorId, ActiveStreamStatus status) {
        return (int) springDataRepository.count(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("workspaceId"), workspaceId),
                        cb.equal(root.get("actorId"), actorId),
                        cb.equal(root.get("streamStatus"), status.name())
                )
        );
    }

    @Override
    public List<AiActiveStream> findByStreamStatusAndExpiresAtBefore(ActiveStreamStatus status, Instant threshold) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("streamStatus"), status.name()),
                        cb.lessThan(root.get("expiresAt"), threshold)
                )
        ).stream().map(mapper::toDomain).toList();
    }
}
