package com.company.scopery.modules.traceability.requirement.http.request;
import jakarta.validation.constraints.NotBlank; import java.util.UUID;
public record CreateRequirementRequest(@NotBlank String title, String code, String description, @NotBlank String requirementType, @NotBlank String priority, UUID applicationId, UUID functionalItemId, UUID nonFunctionalItemId){}
