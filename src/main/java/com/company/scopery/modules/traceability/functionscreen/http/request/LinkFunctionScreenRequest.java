package com.company.scopery.modules.traceability.functionscreen.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkFunctionScreenRequest(@NotNull UUID screenId, String note) {}
