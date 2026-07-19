package com.company.scopery.modules.scope.criteria.http.request;

import jakarta.validation.constraints.NotBlank;

public record WaiveCriteriaRequest(@NotBlank String reason) {}
