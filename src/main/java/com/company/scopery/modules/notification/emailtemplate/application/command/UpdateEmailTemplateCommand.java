package com.company.scopery.modules.notification.emailtemplate.application.command;

import java.util.UUID;

public record UpdateEmailTemplateCommand(
        UUID id,
        String name,
        String description
) {}
