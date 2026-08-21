package com.company.scopery.modules.traceability.requirement.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BulkDeleteRequirementRequest(@NotEmpty List<@NotNull UUID> ids) {}
