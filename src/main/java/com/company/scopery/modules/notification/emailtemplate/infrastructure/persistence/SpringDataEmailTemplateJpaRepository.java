package com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmailTemplateJpaRepository extends JpaRepository<EmailTemplateJpaEntity, UUID> {

    Optional<EmailTemplateJpaEntity> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT e FROM EmailTemplateJpaEntity e
            WHERE (:keyword = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:scope IS NULL OR e.scope = :scope)
              AND (:status IS NULL OR e.status = :status)
              AND (:workspaceId IS NULL OR e.workspaceId = :workspaceId)
              AND (:eventDefinitionId IS NULL OR e.eventDefinitionId = :eventDefinitionId)
              AND e.deletedAt IS NULL
            ORDER BY e.createdAt DESC
            """)
    List<EmailTemplateJpaEntity> search(
            @Param("keyword") String keyword,
            @Param("scope") String scope,
            @Param("status") String status,
            @Param("workspaceId") UUID workspaceId,
            @Param("eventDefinitionId") UUID eventDefinitionId);

    @Query("""
            SELECT COUNT(e) FROM EmailTemplateJpaEntity e
            WHERE (:keyword = '' OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:scope IS NULL OR e.scope = :scope)
              AND (:status IS NULL OR e.status = :status)
              AND (:workspaceId IS NULL OR e.workspaceId = :workspaceId)
              AND (:eventDefinitionId IS NULL OR e.eventDefinitionId = :eventDefinitionId)
              AND e.deletedAt IS NULL
            """)
    long countSearch(
            @Param("keyword") String keyword,
            @Param("scope") String scope,
            @Param("status") String status,
            @Param("workspaceId") UUID workspaceId,
            @Param("eventDefinitionId") UUID eventDefinitionId);
}
