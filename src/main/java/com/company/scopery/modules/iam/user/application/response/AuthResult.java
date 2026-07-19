package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.model.IamUser;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Internal authentication result carrying the authenticated user domain object and the issued JWT tokens")
public record AuthResult(
        @Schema(description = "Authenticated IAM user domain object")
        IamUser user,

        @Schema(description = "Signed JWT access token to be used in Authorization headers", example = "eyJhbGciOiJSUzI1NiJ9...")
        String accessToken,

        @Schema(description = "Opaque refresh token used to obtain a new access token", example = "dGhpcyBpcyBhIHJlZnJlc2g...")
        String refreshToken) {
}
