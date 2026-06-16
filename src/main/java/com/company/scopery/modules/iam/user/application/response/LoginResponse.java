package com.company.scopery.modules.iam.user.application.response;

import java.util.UUID;

public record LoginResponse(
        UUID   userId,
        String username,
        String email,
        String fullName) {
}
