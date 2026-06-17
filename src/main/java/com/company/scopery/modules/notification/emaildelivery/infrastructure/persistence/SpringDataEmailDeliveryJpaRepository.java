package com.company.scopery.modules.notification.emaildelivery.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataEmailDeliveryJpaRepository extends JpaRepository<EmailDeliveryJpaEntity, UUID> {

    @Query("""
            SELECT d FROM EmailDeliveryJpaEntity d
            WHERE (:ruleId IS NULL OR d.ruleId = :ruleId)
              AND (:templateId IS NULL OR d.templateId = :templateId)
              AND (:eventDefinitionId IS NULL OR d.eventDefinitionId = :eventDefinitionId)
              AND (:workspaceId IS NULL OR d.workspaceId = :workspaceId)
              AND (:status IS NULL OR d.status = :status)
            ORDER BY d.createdAt DESC
            """)
    List<EmailDeliveryJpaEntity> search(
            @Param("ruleId") UUID ruleId,
            @Param("templateId") UUID templateId,
            @Param("eventDefinitionId") UUID eventDefinitionId,
            @Param("workspaceId") UUID workspaceId,
            @Param("status") String status);

    @Query("""
            SELECT COUNT(d) FROM EmailDeliveryJpaEntity d
            WHERE (:ruleId IS NULL OR d.ruleId = :ruleId)
              AND (:templateId IS NULL OR d.templateId = :templateId)
              AND (:eventDefinitionId IS NULL OR d.eventDefinitionId = :eventDefinitionId)
              AND (:workspaceId IS NULL OR d.workspaceId = :workspaceId)
              AND (:status IS NULL OR d.status = :status)
            """)
    long countSearch(
            @Param("ruleId") UUID ruleId,
            @Param("templateId") UUID templateId,
            @Param("eventDefinitionId") UUID eventDefinitionId,
            @Param("workspaceId") UUID workspaceId,
            @Param("status") String status);
}
