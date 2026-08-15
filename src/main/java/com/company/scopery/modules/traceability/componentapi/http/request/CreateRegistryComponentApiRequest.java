package com.company.scopery.modules.traceability.componentapi.http.request;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRegistryComponentApiRequest(
        @NotNull UUID apiId,
        @NotNull ComponentApiRole role,
        String note,
        Integer displayOrder
) {}
