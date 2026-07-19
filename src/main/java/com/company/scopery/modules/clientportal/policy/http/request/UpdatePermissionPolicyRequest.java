package com.company.scopery.modules.clientportal.policy.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpdatePermissionPolicyRequest(@NotBlank String name, String description, String permissionsJson) {}
