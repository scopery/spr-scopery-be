package com.company.scopery.modules.traceability.screencomponent.http.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BindComponentToSectionRequest(
        @NotNull UUID componentId,
        Integer displayOrder,
        String note) {}
