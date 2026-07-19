package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiConversation;
import com.company.scopery.modules.aiassistant.domain.model.AiConversationRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiConversationPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiConversationRepository implements AiConversationRepository {

    private final SpringDataAiConversationJpaRepository springDataRepository;
    private final AiConversationPersistenceMapper mapper;

    public JpaAiConversationRepository(SpringDataAiConversationJpaRepository springDataRepository,
                                       AiConversationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiConversation save(AiConversation conversation) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(conversation)));
    }

    @Override
    public Optional<AiConversation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiConversation> findByIdAndOwnerUserId(UUID id, UUID ownerUserId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("id"), id),
                        cb.equal(root.get("ownerUserId"), ownerUserId)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public Page<AiConversation> findByWorkspaceIdAndOwnerUserIdAndStatus(
            UUID workspaceId, UUID ownerUserId, ConversationStatus status, Pageable pageable) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("workspaceId"), workspaceId),
                        cb.equal(root.get("ownerUserId"), ownerUserId),
                        cb.equal(root.get("status"), status.name())
                ),
                pageable
        ).map(mapper::toDomain);
    }

    @Override
    public List<AiConversation> findByStatusAndLastMessageAtBefore(ConversationStatus status, Instant threshold) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("status"), status.name()),
                        cb.lessThan(root.get("lastMessageAt"), threshold)
                )
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiConversation> findByStatusAndDeletedAtBefore(ConversationStatus status, Instant threshold) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("status"), status.name()),
                        cb.lessThan(root.get("deletedAt"), threshold)
                )
        ).stream().map(mapper::toDomain).toList();
    }
}
