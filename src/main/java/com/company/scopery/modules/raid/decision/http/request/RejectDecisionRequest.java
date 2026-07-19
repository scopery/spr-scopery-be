package com.company.scopery.modules.raid.decision.http.request;

import jakarta.validation.constraints.NotBlank;

public record RejectDecisionRequest(@NotBlank String reason) {}
