package com.company.scopery.modules.servicesupport.comment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSupportCommentJpaRepository extends JpaRepository<SupportCommentJpaEntity, UUID> {
    List<SupportCommentJpaEntity> findBySupportCaseIdOrderByCreatedAtAsc(UUID caseId);
}
