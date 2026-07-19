package com.company.scopery.modules.trust.privacy.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreatePrivacyRequestRequest(@NotBlank String requestType, String subjectReference, UUID assignedOwnerUserId) {}
