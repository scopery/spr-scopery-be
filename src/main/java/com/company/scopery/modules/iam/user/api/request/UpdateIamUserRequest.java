package com.company.scopery.modules.iam.user.api.request;

import jakarta.validation.constraints.Size;

public record UpdateIamUserRequest(@Size(max = 255) String fullName) {}
