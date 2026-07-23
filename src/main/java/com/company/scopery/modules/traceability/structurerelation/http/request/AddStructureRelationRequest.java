package com.company.scopery.modules.traceability.structurerelation.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddStructureRelationRequest(
        @NotBlank String fromNodeType,
        @NotNull UUID fromNodeId,
        @NotBlank String toNodeType,
        @NotNull UUID toNodeId,
        String relationType) {
}
