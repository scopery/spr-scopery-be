package com.company.scopery.modules.notification.advanced.digest.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateDigestRuleRequest(@NotBlank String code, @NotBlank String name, String scope, @NotBlank String frequency, String scheduleConfigJson) {}
