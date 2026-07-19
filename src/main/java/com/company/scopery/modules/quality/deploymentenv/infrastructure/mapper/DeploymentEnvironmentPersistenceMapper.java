package com.company.scopery.modules.quality.deploymentenv.infrastructure.mapper;
import com.company.scopery.modules.quality.deploymentenv.domain.enums.EnvironmentType;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironment;
import com.company.scopery.modules.quality.deploymentenv.infrastructure.persistence.DeploymentEnvironmentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeploymentEnvironmentPersistenceMapper {
    public DeploymentEnvironment toDomain(DeploymentEnvironmentJpaEntity e) {
        return new DeploymentEnvironment(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCode(), e.getName(),
                EnvironmentType.valueOf(e.getEnvironmentType()), e.getDescription(), Boolean.TRUE.equals(e.getActive()),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DeploymentEnvironmentJpaEntity toJpaEntity(DeploymentEnvironment d) {
        DeploymentEnvironmentJpaEntity e = new DeploymentEnvironmentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setCode(d.code()); e.setName(d.name()); e.setEnvironmentType(d.environmentType().name());
        e.setDescription(d.description()); e.setActive(d.active());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
