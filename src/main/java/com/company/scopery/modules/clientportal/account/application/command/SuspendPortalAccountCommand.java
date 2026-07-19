package com.company.scopery.modules.clientportal.account.application.command;
import java.util.UUID;
public record SuspendPortalAccountCommand(UUID workspaceId, UUID accountId) {}
