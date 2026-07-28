package com.company.scopery.modules.scope.scopepackage.application.command;

import java.util.List;
import java.util.UUID;

public record BulkLinkRequirementsCommand(UUID projectId, UUID packageId, List<UUID> requirementIds) {}
