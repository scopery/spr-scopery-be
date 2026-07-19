package com.company.scopery.modules.notification.advanced.alert.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateAlertRuleRequest(@NotBlank String ruleCode, @NotBlank String name, String category, String conditionJson, Boolean bypassQuietHours) {}
