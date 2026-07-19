package com.company.scopery.modules.documenthub.share.http.request;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant; import java.util.UUID;
public record CreateDocumentShareRequest(@NotBlank String shareType, @NotBlank String granteeType, UUID granteeId, Instant expiresAt) {}
