package com.company.scopery.modules.trust.privacy.http.request;
import jakarta.validation.constraints.NotBlank;
public record CompletePrivacyRequestRequest(@NotBlank String resolutionSummary) {}
