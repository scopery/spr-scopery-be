package com.company.scopery.modules.project.templateversion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectTemplateVersionJpaRepository
        extends JpaRepository<ProjectTemplateVersionJpaEntity, UUID> {

    List<ProjectTemplateVersionJpaEntity> findByProjectTemplateIdOrderByVersionNumberAsc(UUID projectTemplateId);

    @Query("SELECT MAX(e.versionNumber) FROM ProjectTemplateVersionJpaEntity e WHERE e.projectTemplateId = :templateId")
    Optional<Integer> findMaxVersionNumber(@Param("templateId") UUID templateId);
}
