package com.company.scopery.modules.aicontext.policy.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiContextResolutionPolicyRepository {

    AiContextResolutionPolicy save(AiContextResolutionPolicy policy);

    Optional<AiContextResolutionPolicy> findById(UUID id);

    Optional<AiContextResolutionPolicy> findByWorkspaceIdAndPolicyCode(UUID workspaceId, String policyCode);

    List<AiContextResolutionPolicy> findByWorkspaceId(UUID workspaceId);
}
