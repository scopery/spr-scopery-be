package com.company.scopery.modules.notification.emailrule.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmailRuleJpaRepository extends JpaRepository<EmailRuleJpaEntity, UUID> {

    Optional<EmailRuleJpaEntity> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT r FROM EmailRuleJpaEntity r
            WHERE r.scope = 'SYSTEM'
              AND r.status = 'ACTIVE'
              AND r.enabled = true
              AND r.eventDefinitionId = :eventDefinitionId
              AND r.deletedAt IS NULL
            ORDER BY r.priority ASC
            """)
    List<EmailRuleJpaEntity> findActiveSystemRulesForEvent(@Param("eventDefinitionId") UUID eventDefinitionId);

    @Query("""
            SELECT r FROM EmailRuleJpaEntity r
            WHERE r.scope = 'WORKSPACE'
              AND r.status = 'ACTIVE'
              AND r.enabled = true
              AND r.eventDefinitionId = :eventDefinitionId
              AND r.workspaceId = :workspaceId
              AND r.deletedAt IS NULL
            ORDER BY r.priority ASC
            """)
    List<EmailRuleJpaEntity> findActiveWorkspaceRulesForEvent(
            @Param("eventDefinitionId") UUID eventDefinitionId,
            @Param("workspaceId") UUID workspaceId);

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM EmailRuleJpaEntity r
            WHERE r.eventDefinitionId = :eventDefinitionId
              AND r.status = 'ACTIVE'
              AND r.enabled = true
              AND r.deletedAt IS NULL
            """)
    boolean existsActiveEnabledByEventDefinitionId(@Param("eventDefinitionId") UUID eventDefinitionId);

    @Query("""
            SELECT r FROM EmailRuleJpaEntity r
            WHERE (:keyword = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(r.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:scope IS NULL OR r.scope = :scope)
              AND (:status IS NULL OR r.status = :status)
              AND (:workspaceId IS NULL OR r.workspaceId = :workspaceId)
              AND (:eventDefinitionId IS NULL OR r.eventDefinitionId = :eventDefinitionId)
              AND (:templateId IS NULL OR r.templateId = :templateId)
              AND r.deletedAt IS NULL
            ORDER BY r.priority ASC, r.createdAt DESC
            """)
    List<EmailRuleJpaEntity> search(
            @Param("keyword") String keyword,
            @Param("scope") String scope,
            @Param("status") String status,
            @Param("workspaceId") UUID workspaceId,
            @Param("eventDefinitionId") UUID eventDefinitionId,
            @Param("templateId") UUID templateId);

    @Query("""
            SELECT COUNT(r) FROM EmailRuleJpaEntity r
            WHERE (:keyword = '' OR LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(r.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:scope IS NULL OR r.scope = :scope)
              AND (:status IS NULL OR r.status = :status)
              AND (:workspaceId IS NULL OR r.workspaceId = :workspaceId)
              AND (:eventDefinitionId IS NULL OR r.eventDefinitionId = :eventDefinitionId)
              AND (:templateId IS NULL OR r.templateId = :templateId)
              AND r.deletedAt IS NULL
            """)
    long countSearch(
            @Param("keyword") String keyword,
            @Param("scope") String scope,
            @Param("status") String status,
            @Param("workspaceId") UUID workspaceId,
            @Param("eventDefinitionId") UUID eventDefinitionId,
            @Param("templateId") UUID templateId);
}
