package com.company.scopery.modules.governance.policy.infrastructure.persistence;
import com.company.scopery.modules.governance.policy.domain.model.*;
import com.company.scopery.modules.governance.policy.infrastructure.mapper.GovernancePolicyPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaGovernancePolicyRepository implements GovernancePolicyRepository {
    private final SpringDataGovernancePolicyJpaRepository springData; private final GovernancePolicyPersistenceMapper mapper;
    public JpaGovernancePolicyRepository(SpringDataGovernancePolicyJpaRepository springData, GovernancePolicyPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public GovernancePolicy save(GovernancePolicy p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<GovernancePolicy> findByWorkspaceAndObjectType(UUID workspaceId, String objectTypeCode) {
        return springData.findByWorkspaceIdAndObjectTypeCode(workspaceId, objectTypeCode).map(mapper::toDomain);
    }
    @Override public List<GovernancePolicy> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
