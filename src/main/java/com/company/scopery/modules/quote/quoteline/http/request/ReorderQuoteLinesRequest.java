package com.company.scopery.modules.quote.quoteline.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderQuoteLinesRequest(@NotEmpty List<UUID> lineIds) {}
