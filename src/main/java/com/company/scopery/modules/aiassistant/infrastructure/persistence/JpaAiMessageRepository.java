package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.enums.MessageStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiMessagePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiMessageRepository implements AiMessageRepository {

    private final SpringDataAiMessageJpaRepository springDataRepository;
    private final AiMessagePersistenceMapper mapper;

    public JpaAiMessageRepository(SpringDataAiMessageJpaRepository springDataRepository,
                                  AiMessagePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiMessage save(AiMessage message) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(message)));
    }

    @Override
    public Optional<AiMessage> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiMessage> findByIdAndConversationId(UUID id, UUID conversationId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("id"), id),
                        cb.equal(root.get("conversationId"), conversationId)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public Optional<AiMessage> findByConversationIdAndIdempotencyKey(UUID conversationId, String idempotencyKey) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("conversationId"), conversationId),
                        cb.equal(root.get("idempotencyKey"), idempotencyKey)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public int countByConversationId(UUID conversationId) {
        return (int) springDataRepository.count(
                (root, query, cb) -> cb.equal(root.get("conversationId"), conversationId)
        );
    }

    @Override
    public Page<AiMessage> findByConversationIdAndRoleNotIn(UUID conversationId, List<MessageRole> excludedRoles,
                                                             Pageable pageable) {
        List<String> excludedRoleNames = excludedRoles.stream().map(MessageRole::name).toList();
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("conversationId"), conversationId),
                        root.get("role").in(excludedRoleNames).not()
                ),
                pageable
        ).map(mapper::toDomain);
    }

    @Override
    public List<AiMessage> findByConversationIdAndRoleInOrderBySequenceDesc(UUID conversationId,
                                                                              List<MessageRole> roles, int limit) {
        List<String> roleNames = roles.stream().map(MessageRole::name).toList();
        Specification<AiMessageJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("conversationId"), conversationId));
            predicates.add(root.get("role").in(roleNames));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sequenceInConversation"));
        return springDataRepository.findAll(spec, pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiMessage> findByStatusInAndCreatedAtBefore(List<MessageStatus> statuses, Instant threshold) {
        List<String> statusNames = statuses.stream().map(MessageStatus::name).toList();
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        root.get("status").in(statusNames),
                        cb.lessThan(root.get("createdAt"), threshold)
                )
        ).stream().map(mapper::toDomain).toList();
    }
}
