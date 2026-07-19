package com.company.scopery.modules.quality.deployment.infrastructure.mapper;
import com.company.scopery.modules.quality.deployment.domain.enums.DeploymentStatus;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecord;
import com.company.scopery.modules.quality.deployment.infrastructure.persistence.DeploymentRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeploymentRecordPersistenceMapper {
    public DeploymentRecord toDomain(DeploymentRecordJpaEntity e) {
        return new DeploymentRecord(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getReleasePackageId(), e.getDeploymentEnvironmentId(),
                DeploymentStatus.valueOf(e.getStatus()), e.getBuildReference(), e.getDeploymentReference(),
                e.getStartedAt(), e.getCompletedAt(), e.getDeployedBy(), e.getFailureReason(), e.getRollbackPlanId(),
                e.getRolledBackAt(), e.getRolledBackBy(), e.getRollbackReason(), e.getTraceId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DeploymentRecordJpaEntity toJpaEntity(DeploymentRecord d) {
        DeploymentRecordJpaEntity e = new DeploymentRecordJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId());
        e.setReleasePackageId(d.releasePackageId()); e.setDeploymentEnvironmentId(d.deploymentEnvironmentId());
        e.setStatus(d.status().name()); e.setBuildReference(d.buildReference()); e.setDeploymentReference(d.deploymentReference());
        e.setStartedAt(d.startedAt()); e.setCompletedAt(d.completedAt()); e.setDeployedBy(d.deployedBy());
        e.setFailureReason(d.failureReason()); e.setRollbackPlanId(d.rollbackPlanId());
        e.setRolledBackAt(d.rolledBackAt()); e.setRolledBackBy(d.rolledBackBy()); e.setRollbackReason(d.rollbackReason());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
