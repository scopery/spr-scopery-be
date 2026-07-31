package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderFlowStepsRequest(
        @NotEmpty List<UUID> stepIds
) {}
