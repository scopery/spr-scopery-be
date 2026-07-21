package com.company.scopery.modules.aiaction.application.service;

import com.company.scopery.modules.aiaction.application.response.AiActionToolResponse;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicyRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiActionToolQueryService {

    private final AiActionToolPolicyRepository toolPolicyRepository;

    public AiActionToolQueryService(AiActionToolPolicyRepository toolPolicyRepository) {
        this.toolPolicyRepository = toolPolicyRepository;
    }

    @Transactional(readOnly = true)
    public AiActionToolResponse getToolPolicy(String toolCode, String toolVersion) {
        AiActionToolPolicy policy = toolPolicyRepository.findByToolCodeAndToolVersion(toolCode, toolVersion)
                .orElseThrow(() -> AiActionExceptions.toolNotFound(toolCode, toolVersion));
        return toResponse(policy);
    }

    @Transactional(readOnly = true)
    public List<AiActionToolResponse> listActiveToolPolicies() {
        return toolPolicyRepository.findByStatus(AiActionToolPolicyStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AiActionToolResponse toResponse(AiActionToolPolicy policy) {
        return new AiActionToolResponse(
                policy.id(), policy.toolCode(), policy.toolVersion(),
                policy.invocationScope().name(), policy.riskLevel().name(),
                policy.executionMode().name(), policy.maxBatchTargets(),
                policy.dryRunRequired(), policy.supportsCompensation(),
                policy.supportsPause(), policy.status().name());
    }
}
