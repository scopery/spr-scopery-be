package com.company.scopery.modules.project.project.infrastructure.mapper;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.infrastructure.persistence.ProjectJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectPersistenceMapper {

    public Project toDomain(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getOrganizationId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerUserId(),
                entity.getDefaultCurrency(),
                entity.getPlannedStartDate(),
                entity.getPlannedEndDate(),
                ProjectStatus.valueOf(entity.getStatus()),
                entity.getActivatedAt(),
                entity.getActivatedBy(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getSourceTemplateId(),
                entity.getSourceTemplateVersionId(),
                entity.getSourceTemplateAppliedAt(),
                entity.getCurrentScheduleRunId(),
                entity.getCurrentEstimationRunId(),
                entity.getCurrentFinanceScenarioId(),
                entity.getCurrentQuoteId(),
                entity.getCurrentQuoteVersionId(),
                entity.getCurrentBaselineId(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ProjectJpaEntity toJpaEntity(Project domain) {
        ProjectJpaEntity entity = new ProjectJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setOrganizationId(domain.organizationId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setOwnerUserId(domain.ownerUserId());
        entity.setDefaultCurrency(domain.defaultCurrency());
        entity.setPlannedStartDate(domain.plannedStartDate());
        entity.setPlannedEndDate(domain.plannedEndDate());
        entity.setStatus(domain.status().name());
        entity.setActivatedAt(domain.activatedAt());
        entity.setActivatedBy(domain.activatedBy());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        entity.setSourceTemplateId(domain.sourceTemplateId());
        entity.setSourceTemplateVersionId(domain.sourceTemplateVersionId());
        entity.setSourceTemplateAppliedAt(domain.sourceTemplateAppliedAt());
        entity.setCurrentScheduleRunId(domain.currentScheduleRunId());
        entity.setCurrentEstimationRunId(domain.currentEstimationRunId());
        entity.setCurrentFinanceScenarioId(domain.currentFinanceScenarioId());
        entity.setCurrentQuoteId(domain.currentQuoteId());
        entity.setCurrentQuoteVersionId(domain.currentQuoteVersionId());
        entity.setCurrentBaselineId(domain.currentBaselineId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        // For new entities (createdAt == null), leave version null so Hibernate treats
        // the entity as TRANSIENT and initializes @Version to 0 on INSERT.
        return entity;
    }
}
