package com.company.scopery.modules.governance.versioning.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateRestoreRequest(@NotBlank String objectTypeCode, @NotNull UUID targetId, @NotNull UUID restoreFromVersionRecordId, String reason) {}
