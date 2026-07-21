package com.company.scopery.modules.aiaction.tool.domain.model;

import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionToolPolicyRepository {

    AiActionToolPolicy save(AiActionToolPolicy policy);

    Optional<AiActionToolPolicy> findByToolCodeAndToolVersion(String toolCode, String toolVersion);

    List<AiActionToolPolicy> findByStatus(AiActionToolPolicyStatus status);

    List<AiActionToolPolicy> findByInvocationScopeAndStatus(AiActionInvocationScope scope, AiActionToolPolicyStatus status);
}
