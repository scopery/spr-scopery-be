package com.company.scopery.modules.traceability.requirement.http.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record LinkTestCasesRequest(@NotEmpty List<UUID> testCaseIds) {}
