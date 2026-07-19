package com.company.scopery.modules.governance.lock.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateObjectLockRequest(@NotBlank String objectTypeCode, @NotNull UUID targetId, @NotBlank String lockType, String reason) {}
