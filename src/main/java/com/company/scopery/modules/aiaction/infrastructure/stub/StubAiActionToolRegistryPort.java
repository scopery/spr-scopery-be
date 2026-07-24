package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import java.util.List;
import java.util.Optional;

// Stub implementation — replaced by JpaAiActionToolRegistryPort
public class StubAiActionToolRegistryPort implements AiActionToolRegistryPort {

    @Override
    public Optional<AiActionToolPolicy> findPolicy(String toolCode, String toolVersion) {
        return Optional.empty();
    }

    @Override
    public AiActionToolPolicy requirePolicy(String toolCode, String toolVersion) {
        throw AiActionExceptions.toolNotFound(toolCode, toolVersion);
    }

    @Override
    public AiActionToolAdapter requireAdapter(String toolCode, String toolVersion) {
        throw AiActionExceptions.toolAdapterNotFound(toolCode);
    }

    @Override
    public List<AiActionToolPolicy> listActivePolicies(AiActionInvocationScope scope) {
        return List.of();
    }

    @Override
    public List<AiActionToolPolicy> listAllActivePolicies() {
        return List.of();
    }

    @Override
    public List<AiActionToolPolicy> listLlmCallablePolicies() {
        return List.of();
    }
}
