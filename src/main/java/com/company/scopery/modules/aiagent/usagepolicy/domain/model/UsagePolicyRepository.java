package com.company.scopery.modules.aiagent.usagepolicy.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsagePolicyRepository {

    UsagePolicy save(UsagePolicy policy);

    Optional<UsagePolicy> findById(UUID id);

    boolean existsByCode(UsagePolicyCode code);

    boolean existsActiveByTargetTypeAndTargetId(UsagePolicyTargetType targetType, UUID targetId, UUID excludeId);

    PageResult<UsagePolicy> findAll(String keyword, UsagePolicyTargetType targetType,
                              UsagePolicyStatus status, PageQuery pageQuery);

    List<UsagePolicy> findApplicableActivePolicies(UUID eventConfigId, UUID agentId, UUID modelDeploymentId);
}
