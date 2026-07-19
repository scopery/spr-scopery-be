package com.company.scopery.modules.trust.consent.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateContactSuppressionRequest(@NotBlank String suppressionType, String reason,
        UUID externalContactId, UUID portalAccountId) {}
