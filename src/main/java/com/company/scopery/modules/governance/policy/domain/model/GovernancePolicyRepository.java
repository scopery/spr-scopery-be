package com.company.scopery.modules.governance.policy.domain.model;
import java.util.*; import java.util.UUID;
public interface GovernancePolicyRepository {
    GovernancePolicy save(GovernancePolicy p);
    Optional<GovernancePolicy> findByWorkspaceAndObjectType(UUID workspaceId, String objectTypeCode);
    List<GovernancePolicy> findByWorkspaceId(UUID workspaceId);
}
