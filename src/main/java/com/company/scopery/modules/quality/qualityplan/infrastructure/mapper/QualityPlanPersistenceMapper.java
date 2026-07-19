package com.company.scopery.modules.quality.qualityplan.infrastructure.mapper;
import com.company.scopery.modules.quality.qualityplan.domain.enums.QualityPlanStatus;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlan;
import com.company.scopery.modules.quality.qualityplan.infrastructure.persistence.QualityPlanJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class QualityPlanPersistenceMapper {
    public QualityPlan toDomain(QualityPlanJpaEntity e) {
        return new QualityPlan(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getSourceBaselineId(), e.getCode(),
                e.getName(), e.getDescription(), QualityPlanStatus.valueOf(e.getStatus()), e.isCurrentFlag(),
                e.getQualityObjectives(), e.getTestStrategy(), e.getEntryCriteria(), e.getExitCriteria(),
                e.getDefectPolicyJson(), e.getReleaseReadinessPolicyJson(), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public QualityPlanJpaEntity toJpaEntity(QualityPlan d) {
        QualityPlanJpaEntity e = new QualityPlanJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setSourceBaselineId(d.sourceBaselineId()); e.setCode(d.code()); e.setName(d.name());
        e.setDescription(d.description()); e.setStatus(d.status().name()); e.setCurrentFlag(d.currentFlag());
        e.setQualityObjectives(d.qualityObjectives()); e.setTestStrategy(d.testStrategy());
        e.setEntryCriteria(d.entryCriteria()); e.setExitCriteria(d.exitCriteria());
        e.setDefectPolicyJson(d.defectPolicyJson()); e.setReleaseReadinessPolicyJson(d.releaseReadinessPolicyJson());
        e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy()); e.setArchivedAt(d.archivedAt());
        e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
