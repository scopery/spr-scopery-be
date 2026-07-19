package com.company.scopery.modules.quality.deployment.infrastructure.persistence;
import com.company.scopery.modules.quality.deployment.domain.model.*;
import com.company.scopery.modules.quality.deployment.infrastructure.mapper.DeploymentRecordPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDeploymentRecordRepository implements DeploymentRecordRepository {
    private final SpringDataDeploymentRecordJpaRepository springData;
    private final DeploymentRecordPersistenceMapper mapper;
    public JpaDeploymentRecordRepository(SpringDataDeploymentRecordJpaRepository springData, DeploymentRecordPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public DeploymentRecord save(DeploymentRecord e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<DeploymentRecord> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<DeploymentRecord> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
