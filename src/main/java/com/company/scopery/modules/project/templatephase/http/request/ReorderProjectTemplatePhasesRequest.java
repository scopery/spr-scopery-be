package com.company.scopery.modules.project.templatephase.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderProjectTemplatePhasesRequest(
        @NotEmpty List<UUID> orderedPhaseIds
) {}
