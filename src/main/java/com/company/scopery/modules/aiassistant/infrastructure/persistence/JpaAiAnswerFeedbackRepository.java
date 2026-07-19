package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedback;
import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedbackRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiAnswerFeedbackPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiAnswerFeedbackRepository implements AiAnswerFeedbackRepository {

    private final SpringDataAiAnswerFeedbackJpaRepository springDataRepository;
    private final AiAnswerFeedbackPersistenceMapper mapper;

    public JpaAiAnswerFeedbackRepository(SpringDataAiAnswerFeedbackJpaRepository springDataRepository,
                                         AiAnswerFeedbackPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiAnswerFeedback save(AiAnswerFeedback feedback) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(feedback)));
    }

    @Override
    public Optional<AiAnswerFeedback> findByMessageIdAndActorId(UUID messageId, UUID actorId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("messageId"), messageId),
                        cb.equal(root.get("actorId"), actorId)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public List<AiAnswerFeedback> findByConversationId(UUID conversationId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("conversationId"), conversationId)
        ).stream().map(mapper::toDomain).toList();
    }
}
