package com.company.scopery.modules.aiassistant.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiAnswerFeedbackRepository {

    AiAnswerFeedback save(AiAnswerFeedback feedback);

    Optional<AiAnswerFeedback> findByMessageIdAndActorId(UUID messageId, UUID actorId);

    List<AiAnswerFeedback> findByConversationId(UUID conversationId);
}
