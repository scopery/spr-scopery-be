package com.company.scopery.modules.quality.rollbackplan.infrastructure.mapper;
import com.company.scopery.modules.quality.rollbackplan.domain.enums.RollbackPlanStatus;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlan;
import com.company.scopery.modules.quality.rollbackplan.infrastructure.persistence.RollbackPlanJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RollbackPlanPersistenceMapper {
    public RollbackPlan toDomain(RollbackPlanJpaEntity e) {
        return new RollbackPlan(e.getId(), e.getProjectId(), e.getReleasePackageId(), e.getDeploymentRecordId(), e.getTitle(), e.getDescription(),
                e.getOwnerUserId(), RollbackPlanStatus.valueOf(e.getStatus()), e.getStepsJson(), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RollbackPlanJpaEntity toJpaEntity(RollbackPlan d) {
        RollbackPlanJpaEntity e = new RollbackPlanJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setReleasePackageId(d.releasePackageId()); e.setDeploymentRecordId(d.deploymentRecordId());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setOwnerUserId(d.ownerUserId()); e.setStatus(d.status().name());
        e.setStepsJson(d.stepsJson()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
