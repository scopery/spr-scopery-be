package com.company.scopery.modules.raid.decision.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDecisionRequest(@NotBlank String title, @NotBlank String rationale) {}
