package com.company.scopery.modules.aicontext.policy.application.command;

import java.util.UUID;

public record CreateAiContextPolicyCommand(UUID workspaceId, String policyCode, String label, int maxTokens, boolean includeRelated) {}
