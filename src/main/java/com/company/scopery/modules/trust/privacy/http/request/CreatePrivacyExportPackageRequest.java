package com.company.scopery.modules.trust.privacy.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreatePrivacyExportPackageRequest(@NotNull UUID privacyRequestId, String packageManifestJson) {}
