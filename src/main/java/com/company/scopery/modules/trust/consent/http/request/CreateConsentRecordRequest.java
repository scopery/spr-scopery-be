package com.company.scopery.modules.trust.consent.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateConsentRecordRequest(@NotBlank String consentType, String source,
        String sourceReference, UUID externalContactId, UUID portalAccountId) {}
