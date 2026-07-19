package com.company.scopery.modules.clientportal.feedback.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataClientFeedbackJpaRepository extends JpaRepository<ClientFeedbackJpaEntity, UUID> {
    Optional<ClientFeedbackJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ClientFeedbackJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
