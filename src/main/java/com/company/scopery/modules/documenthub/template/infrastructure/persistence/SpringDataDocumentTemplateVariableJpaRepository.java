package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentTemplateVariableJpaRepository
        extends JpaRepository<DocumentTemplateVariableJpaEntity, UUID> {
    List<DocumentTemplateVariableJpaEntity> findByTemplateVersionIdOrderByOrdinalAsc(UUID templateVersionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM DocumentTemplateVariableJpaEntity e WHERE e.templateVersionId = :templateVersionId")
    void deleteByTemplateVersionId(@Param("templateVersionId") UUID templateVersionId);
}
