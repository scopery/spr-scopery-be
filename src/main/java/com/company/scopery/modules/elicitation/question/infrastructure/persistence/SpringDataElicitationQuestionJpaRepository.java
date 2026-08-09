package com.company.scopery.modules.elicitation.question.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataElicitationQuestionJpaRepository
        extends JpaRepository<ElicitationQuestionJpaEntity, UUID> {

    List<ElicitationQuestionJpaEntity> findBySessionIdOrderBySequenceAsc(UUID sessionId);

    List<ElicitationQuestionJpaEntity> findByRoundIdOrderBySequenceAsc(UUID roundId);

    int countBySessionId(UUID sessionId);

    @Query("SELECT COALESCE(MAX(q.sequence), 0) FROM ElicitationQuestionJpaEntity q WHERE q.sessionId = :sessionId")
    int findMaxSequenceBySessionId(@Param("sessionId") UUID sessionId);
}
