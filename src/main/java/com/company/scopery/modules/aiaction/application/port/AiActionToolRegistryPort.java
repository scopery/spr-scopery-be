package com.company.scopery.modules.aiaction.application.port;

import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;

import java.util.List;
import java.util.Optional;

public interface AiActionToolRegistryPort {

    Optional<AiActionToolPolicy> findPolicy(String toolCode, String toolVersion);

    AiActionToolPolicy requirePolicy(String toolCode, String toolVersion);

    AiActionToolAdapter requireAdapter(String toolCode, String toolVersion);

    List<AiActionToolPolicy> listActivePolicies(AiActionInvocationScope scope);

    List<AiActionToolPolicy> listAllActivePolicies();
}
