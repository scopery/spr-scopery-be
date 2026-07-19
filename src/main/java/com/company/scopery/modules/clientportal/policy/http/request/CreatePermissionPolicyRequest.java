package com.company.scopery.modules.clientportal.policy.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreatePermissionPolicyRequest(@NotBlank String code, @NotBlank String name, String description, String permissionsJson) {}
