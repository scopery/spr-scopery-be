package com.company.scopery.modules.resourcecapacity.dayrule.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReplaceDayRulesRequest(
        @NotEmpty @Valid List<DayRuleItemRequest> dayRules
) {}
