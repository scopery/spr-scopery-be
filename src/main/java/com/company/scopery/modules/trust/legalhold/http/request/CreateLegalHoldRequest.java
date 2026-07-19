package com.company.scopery.modules.trust.legalhold.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateLegalHoldRequest(@NotBlank String holdType, @NotBlank String scopeType, UUID scopeId, @NotBlank String reason) {}
