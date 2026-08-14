package com.company.scopery.modules.traceability.screenspecdoc.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddScreenToSpecDocRequest(
        @NotNull UUID screenId,
        int displayOrder,
        String note) {}
