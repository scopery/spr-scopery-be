package com.company.scopery.modules.servicesupport.escalation.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateEscalationRuleRequest(@NotBlank String ruleCode, @NotBlank String name, @NotBlank String triggerType) {}
