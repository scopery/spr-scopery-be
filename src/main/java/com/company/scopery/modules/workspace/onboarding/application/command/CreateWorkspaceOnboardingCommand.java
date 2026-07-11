package com.company.scopery.modules.workspace.onboarding.application.command;

public record CreateWorkspaceOnboardingCommand(
        String organizationName,
        String organizationCode,
        String workspaceName,
        String workspaceCode,
        String workspaceDescription
) {}
