package com.company.scopery.modules.governance.grant.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateAccessGrantRequest(@NotBlank String objectTypeCode, @NotNull UUID targetId, @NotBlank String granteeType, @NotNull UUID granteeId, @NotBlank String grantRole) {}
