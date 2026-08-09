package com.company.scopery.modules.elicitation.session.application.response;

import java.util.UUID;

public record ScopeLockResponse(
        boolean locked,
        UUID sessionId
) {}
