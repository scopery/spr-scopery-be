package com.company.scopery.modules.elicitation.session.application.response;

import java.util.List;

public record ScopeTreeResponse(
        List<ScopeTreeEntityResponse> before,
        List<ScopeTreeEntityResponse> after
) {}
