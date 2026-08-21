package com.company.scopery.modules.traceability.usecase.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BulkDeleteUseCaseRequest(@NotEmpty List<@NotNull UUID> ids) {}
