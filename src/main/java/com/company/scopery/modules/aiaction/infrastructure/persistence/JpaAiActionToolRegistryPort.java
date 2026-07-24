package com.company.scopery.modules.aiaction.infrastructure.persistence;

import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.application.registry.AiActionToolAdapterRegistry;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaAiActionToolRegistryPort implements AiActionToolRegistryPort {

    private final AiActionToolPolicyRepository policyRepository;
    private final AiActionToolAdapterRegistry adapterRegistry;

    public JpaAiActionToolRegistryPort(AiActionToolPolicyRepository policyRepository,
                                        AiActionToolAdapterRegistry adapterRegistry) {
        this.policyRepository = policyRepository;
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public Optional<AiActionToolPolicy> findPolicy(String toolCode, String toolVersion) {
        return policyRepository.findByToolCodeAndToolVersion(toolCode, toolVersion);
    }

    @Override
    public AiActionToolPolicy requirePolicy(String toolCode, String toolVersion) {
        return policyRepository.findByToolCodeAndToolVersion(toolCode, toolVersion)
                .orElseThrow(() -> AiActionExceptions.toolNotFound(toolCode, toolVersion));
    }

    @Override
    public AiActionToolAdapter requireAdapter(String toolCode, String toolVersion) {
        AiActionToolAdapter adapter = adapterRegistry.getAdapter(toolCode, toolVersion);
        if (adapter == null) {
            throw AiActionExceptions.toolAdapterNotFound(toolCode);
        }
        return adapter;
    }

    @Override
    public List<AiActionToolPolicy> listActivePolicies(AiActionInvocationScope scope) {
        return policyRepository.findByInvocationScopeAndStatus(scope, AiActionToolPolicyStatus.ACTIVE);
    }

    @Override
    public List<AiActionToolPolicy> listAllActivePolicies() {
        return policyRepository.findByStatus(AiActionToolPolicyStatus.ACTIVE);
    }

    @Override
    public List<AiActionToolPolicy> listLlmCallablePolicies() {
        List<AiActionToolPolicy> callable = new java.util.ArrayList<>(
                policyRepository.findByInvocationScopeAndStatus(
                        AiActionInvocationScope.LLM_CALLABLE, AiActionToolPolicyStatus.ACTIVE));
        callable.addAll(policyRepository.findByInvocationScopeAndStatus(
                AiActionInvocationScope.LLM_CALLABLE_READ_ONLY, AiActionToolPolicyStatus.ACTIVE));
        return callable;
    }
}
