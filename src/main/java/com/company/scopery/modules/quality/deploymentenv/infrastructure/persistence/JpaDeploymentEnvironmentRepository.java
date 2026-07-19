package com.company.scopery.modules.quality.deploymentenv.infrastructure.persistence;
import com.company.scopery.modules.quality.deploymentenv.domain.model.*;
import com.company.scopery.modules.quality.deploymentenv.infrastructure.mapper.DeploymentEnvironmentPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDeploymentEnvironmentRepository implements DeploymentEnvironmentRepository {
    private final SpringDataDeploymentEnvironmentJpaRepository springData;
    private final DeploymentEnvironmentPersistenceMapper mapper;
    public JpaDeploymentEnvironmentRepository(SpringDataDeploymentEnvironmentJpaRepository springData, DeploymentEnvironmentPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DeploymentEnvironment save(DeploymentEnvironment e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DeploymentEnvironment> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DeploymentEnvironment> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
