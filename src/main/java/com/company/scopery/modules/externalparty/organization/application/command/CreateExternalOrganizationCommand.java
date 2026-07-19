package com.company.scopery.modules.externalparty.organization.application.command;
import java.util.UUID;
public record CreateExternalOrganizationCommand(UUID workspaceId, String code, String name, String organizationType, String taxId, String website, String notes) {}
