package com.company.scopery.modules.clientportal.account.application.command;
import java.util.UUID;
public record DeactivatePortalAccountCommand(UUID workspaceId, UUID accountId) {}
