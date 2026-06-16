package com.company.scopery.modules.aiagent.playground.application.response;

import java.util.List;
import java.util.UUID;

public record PlaygroundOptionResponse(
        List<EventConfigItem> eventConfigs,
        List<AgentItem> agents,
        List<PromptVersionItem> promptVersions,
        List<ModelDeploymentItem> modelDeployments
) {
    public record EventConfigItem(UUID id, String code, String name, String status, String environment) {}
    public record AgentItem(UUID id, String code, String name, String status) {}
    public record PromptVersionItem(UUID id, UUID templateId, int versionNumber, String status) {}
    public record ModelDeploymentItem(UUID id, String code, String name, String status, String environment) {}
}
