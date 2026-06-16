package com.company.scopery.modules.iam.user.application.response;

import com.company.scopery.modules.iam.user.domain.IamUser;

public record AuthResult(IamUser user, String accessToken, String refreshToken) {
}
