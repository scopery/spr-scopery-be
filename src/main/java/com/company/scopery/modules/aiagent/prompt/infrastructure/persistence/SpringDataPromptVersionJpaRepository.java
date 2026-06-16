package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataPromptVersionJpaRepository
        extends JpaRepository<PromptVersionJpaEntity, UUID>,
                JpaSpecificationExecutor<PromptVersionJpaEntity> {

    @Query("SELECT COALESCE(MAX(e.versionNumber), 0) FROM PromptVersionJpaEntity e WHERE e.templateId = :templateId")
    int getMaxVersionNumber(@Param("templateId") UUID templateId);

    List<PromptVersionJpaEntity> findByStatus(String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE PromptVersionJpaEntity e
           SET e.status = 'ARCHIVED',
               e.updatedAt = CURRENT_TIMESTAMP
           WHERE e.templateId = :templateId
             AND e.status = 'ACTIVE'
             AND e.id <> :excludeId
           """)
    int archiveOtherActiveVersions(@Param("templateId") UUID templateId,
                                   @Param("excludeId") UUID excludeId);
}