package com.company.scopery.modules.notification.advanced.reminder.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateReminderRuleRequest(@NotBlank String ruleCode, @NotBlank String name, String conditionJson, String recipientRuleJson) {}
