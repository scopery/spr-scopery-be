package com.company.scopery.modules.aiplanning.applyresult.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataAiPlanningApplyResultJpaRepository extends JpaRepository<AiPlanningApplyResultJpaEntity, UUID> {
    List<AiPlanningApplyResultJpaEntity> findBySuggestionIdOrderByCreatedAtAsc(UUID suggestionId);
}
