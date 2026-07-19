package com.company.scopery.modules.quality.defect.infrastructure.mapper;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import com.company.scopery.modules.quality.defect.infrastructure.persistence.DefectJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DefectPersistenceMapper {
    public Defect toDomain(DefectJpaEntity e) {
        return new Defect(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getCode(), e.getTitle(), e.getDescription(),
                DefectCategory.valueOf(e.getCategory()), DefectSeverity.valueOf(e.getSeverity()), DefectPriority.valueOf(e.getPriority()),
                DefectStatus.valueOf(e.getStatus()), e.getAssignedToUserId(), e.getReportedBy(), e.getReportedAt(),
                e.getReproductionSteps(), e.getExpectedResult(), e.getActualResult(), e.getEnvironmentNotes(),
                e.getResolutionType() == null ? null : DefectResolutionType.valueOf(e.getResolutionType()),
                e.getResolutionNote(), e.getResolvedAt(), e.getResolvedBy(), e.getClosedAt(), e.getClosedBy(),
                e.getReopenedAt(), e.getReopenedBy(), e.getReopenReason(), e.getSourceTestCaseResultId(), e.getSourceAiSuggestionId(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public DefectJpaEntity toJpaEntity(Defect d) {
        DefectJpaEntity e = new DefectJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setCategory(d.category().name());
        e.setSeverity(d.severity().name()); e.setPriority(d.priority().name()); e.setStatus(d.status().name());
        e.setAssignedToUserId(d.assignedToUserId()); e.setReportedBy(d.reportedBy()); e.setReportedAt(d.reportedAt());
        e.setReproductionSteps(d.reproductionSteps()); e.setExpectedResult(d.expectedResult()); e.setActualResult(d.actualResult());
        e.setEnvironmentNotes(d.environmentNotes());
        e.setResolutionType(d.resolutionType() == null ? null : d.resolutionType().name());
        e.setResolutionNote(d.resolutionNote()); e.setResolvedAt(d.resolvedAt()); e.setResolvedBy(d.resolvedBy());
        e.setClosedAt(d.closedAt()); e.setClosedBy(d.closedBy()); e.setReopenedAt(d.reopenedAt()); e.setReopenedBy(d.reopenedBy());
        e.setReopenReason(d.reopenReason()); e.setSourceTestCaseResultId(d.sourceTestCaseResultId());
        e.setSourceAiSuggestionId(d.sourceAiSuggestionId()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
