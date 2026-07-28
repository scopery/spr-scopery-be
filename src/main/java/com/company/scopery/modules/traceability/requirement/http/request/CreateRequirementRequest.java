package com.company.scopery.modules.traceability.requirement.http.request;
public record CreateRequirementRequest(
        @jakarta.validation.constraints.NotBlank String title,
        String code,
        String description,
        @jakarta.validation.constraints.NotBlank String requirementType,
        @jakarta.validation.constraints.NotBlank String priority,
        java.util.UUID applicationId,
        java.util.UUID functionalItemId,
        java.util.UUID nonFunctionalItemId,
        java.util.UUID scopeItemId,
        java.util.UUID scopePackageId){}
