package com.company.scopery.modules.resourcecapacity.threshold.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.*;
import com.company.scopery.modules.resourcecapacity.threshold.infrastructure.mapper.UtilizationThresholdPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional; import java.util.UUID;
@Repository
public class JpaUtilizationThresholdPolicyRepository implements UtilizationThresholdPolicyRepository {
    private final SpringDataUtilizationThresholdPolicyJpaRepository spring; private final UtilizationThresholdPolicyPersistenceMapper mapper;
    public JpaUtilizationThresholdPolicyRepository(SpringDataUtilizationThresholdPolicyJpaRepository spring, UtilizationThresholdPolicyPersistenceMapper mapper) {
        this.spring=spring; this.mapper=mapper;
    }
    @Override public UtilizationThresholdPolicy save(UtilizationThresholdPolicy p) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<UtilizationThresholdPolicy> findByWorkspaceIdAndProjectIdIsNull(UUID workspaceId) {
        return spring.findFirstByWorkspaceIdAndProjectIdIsNull(workspaceId).map(mapper::toDomain);
    }
    @Override public Optional<UtilizationThresholdPolicy> findByProjectId(UUID projectId) {
        return spring.findFirstByProjectId(projectId).map(mapper::toDomain);
    }
}
