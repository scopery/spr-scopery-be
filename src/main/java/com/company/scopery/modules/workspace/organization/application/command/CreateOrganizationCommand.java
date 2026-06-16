package com.company.scopery.modules.workspace.organization.application.command;

public record CreateOrganizationCommand(
        String name,
        String code,
        String description) {
}
