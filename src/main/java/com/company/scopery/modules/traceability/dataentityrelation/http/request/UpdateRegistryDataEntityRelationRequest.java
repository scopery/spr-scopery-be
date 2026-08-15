package com.company.scopery.modules.traceability.dataentityrelation.http.request;

import com.company.scopery.modules.traceability.dataentityrelation.domain.enums.DataEntityRelationType;
import jakarta.validation.constraints.NotNull;

public record UpdateRegistryDataEntityRelationRequest(
        @NotNull DataEntityRelationType relationType,
        String sourceColumn,
        String label,
        String note) {}
