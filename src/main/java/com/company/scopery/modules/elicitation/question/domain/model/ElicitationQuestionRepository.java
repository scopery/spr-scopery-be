package com.company.scopery.modules.elicitation.question.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElicitationQuestionRepository {

    ElicitationQuestion save(ElicitationQuestion question);

    List<ElicitationQuestion> saveAll(List<ElicitationQuestion> questions);

    Optional<ElicitationQuestion> findById(UUID id);

    List<ElicitationQuestion> findAllBySessionId(UUID sessionId);

    int countBySessionId(UUID sessionId);

    int findMaxSequenceBySessionId(UUID sessionId);
}
