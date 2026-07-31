package com.company.scopery.modules.quality.verificationcase.infrastructure.mapper;
import com.company.scopery.modules.quality.verificationcase.domain.enums.*;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCase;
import com.company.scopery.modules.quality.verificationcase.infrastructure.persistence.VerificationCaseJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class VerificationCasePersistenceMapper {
    public VerificationCase toDomain(VerificationCaseJpaEntity e) {
        return new VerificationCase(
                e.getId(), e.getProjectId(), e.getRequirementId(),
                e.getCode(), e.getTitle(), e.getDescription(),
                VerificationMethod.valueOf(e.getVerificationMethod()),
                e.getProcedure(), e.getExpectedResultJson(), e.getEnvironment(),
                VerificationCaseStatus.valueOf(e.getLifecycleStatus()),
                e.getAutomationStatus() != null ? VerificationAutomationStatus.valueOf(e.getAutomationStatus()) : VerificationAutomationStatus.MANUAL,
                e.getOwnerId(), e.getAssigneeId(),
                e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public VerificationCaseJpaEntity toJpaEntity(VerificationCase d) {
        VerificationCaseJpaEntity e = new VerificationCaseJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setRequirementId(d.requirementId());
        e.setCode(d.code()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setVerificationMethod(d.verificationMethod().name());
        e.setProcedure(d.procedure()); e.setExpectedResultJson(d.expectedResultJson());
        e.setEnvironment(d.environment());
        e.setLifecycleStatus(d.lifecycleStatus().name());
        e.setAutomationStatus(d.automationStatus() != null ? d.automationStatus().name() : VerificationAutomationStatus.MANUAL.name());
        e.setOwnerId(d.ownerId()); e.setAssigneeId(d.assigneeId());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version() >= 0 ? d.version() : null);
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
