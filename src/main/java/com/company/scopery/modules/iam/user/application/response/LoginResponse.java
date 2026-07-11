package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.model.IamUser;

import java.util.UUID;

public record LoginResponse(
        UUID   userId,
        String username,
        String email,
        String fullName) {

    public static LoginResponse from(IamUser user) {
        return new LoginResponse(user.id(), user.username().value(), user.email().value(), user.fullName());
    }
}
