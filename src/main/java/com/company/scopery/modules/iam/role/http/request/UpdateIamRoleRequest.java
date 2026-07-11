package com.company.scopery.modules.iam.role.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateIamRoleRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2000) String description) {}
