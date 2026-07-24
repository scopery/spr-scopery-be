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

    /** Returns all active policies callable by the LLM (LLM_CALLABLE + LLM_CALLABLE_READ_ONLY). */
    List<AiActionToolPolicy> listLlmCallablePolicies();
}
