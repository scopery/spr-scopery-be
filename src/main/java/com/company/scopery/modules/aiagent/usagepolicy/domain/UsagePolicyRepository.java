package com.company.scopery.modules.aiagent.usagepolicy.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsagePolicyRepository {

    UsagePolicy save(UsagePolicy policy);

    Optional<UsagePolicy> findById(UUID id);

    boolean existsByCode(UsagePolicyCode code);

    boolean existsActiveByTargetTypeAndTargetId(UsagePolicyTargetType targetType, UUID targetId, UUID excludeId);

    Page<UsagePolicy> findAll(String keyword, UsagePolicyTargetType targetType,
                              UsagePolicyStatus status, Pageable pageable);

    List<UsagePolicy> findApplicableActivePolicies(UUID eventConfigId, UUID agentId, UUID modelDeploymentId);
}