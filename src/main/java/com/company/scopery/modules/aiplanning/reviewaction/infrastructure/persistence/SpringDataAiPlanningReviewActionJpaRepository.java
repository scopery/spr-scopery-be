package com.company.scopery.modules.aiplanning.reviewaction.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataAiPlanningReviewActionJpaRepository extends JpaRepository<AiPlanningReviewActionJpaEntity, UUID> {
    List<AiPlanningReviewActionJpaEntity> findBySuggestionIdOrderByCreatedAtAsc(UUID suggestionId);
}
