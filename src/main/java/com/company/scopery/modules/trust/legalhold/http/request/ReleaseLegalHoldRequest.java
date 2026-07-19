package com.company.scopery.modules.trust.legalhold.http.request;
import jakarta.validation.constraints.NotBlank;
public record ReleaseLegalHoldRequest(@NotBlank String releaseReason) {}
