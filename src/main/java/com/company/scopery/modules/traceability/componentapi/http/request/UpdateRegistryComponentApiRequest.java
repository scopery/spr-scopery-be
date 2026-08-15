package com.company.scopery.modules.traceability.componentapi.http.request;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRegistryComponentApiRequest(
        @NotNull ComponentApiRole role,
        String note,
        Integer displayOrder
) {}
