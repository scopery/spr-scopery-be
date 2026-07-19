package com.company.scopery.modules.aicontext.policy.application.response;

import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicy;

import java.util.UUID;

public record AiContextPolicyResponse(UUID id, UUID workspaceId, String policyCode, String label, int maxTokens, boolean includeRelated, boolean enabled) {
    public static AiContextPolicyResponse from(AiContextResolutionPolicy p) {
        return new AiContextPolicyResponse(p.id(), p.workspaceId(), p.policyCode(), p.label(), p.maxTokens(), p.includeRelated(), p.enabled());
    }
}
