package com.company.scopery.modules.elicitation.session.application.command;

import java.util.UUID;

public record StartSessionCommand(
        UUID projectId,
        UUID scopePackageId,
        String title
) {}
