package com.company.scopery.modules.resourcecapacity.resourcerole.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateResourceRoleRequest(@NotBlank String roleCode, @NotBlank String name, String description, UUID defaultRateCardId) {}
