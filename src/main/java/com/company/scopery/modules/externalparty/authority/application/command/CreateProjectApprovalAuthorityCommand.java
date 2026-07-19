package com.company.scopery.modules.externalparty.authority.application.command;
import java.util.UUID;
public record CreateProjectApprovalAuthorityCommand(UUID projectId, UUID stakeholderId, String authorityType, String notes) {}
