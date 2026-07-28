package com.company.scopery.modules.scope.scopepackage.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record BulkLinkRequirementsRequest(@NotEmpty List<UUID> requirementIds) {}
