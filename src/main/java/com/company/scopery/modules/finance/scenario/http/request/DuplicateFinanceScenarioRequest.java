package com.company.scopery.modules.finance.scenario.http.request;

import jakarta.validation.constraints.NotBlank;

public record DuplicateFinanceScenarioRequest(
        @NotBlank String newCode,
        String newName
) {
}
