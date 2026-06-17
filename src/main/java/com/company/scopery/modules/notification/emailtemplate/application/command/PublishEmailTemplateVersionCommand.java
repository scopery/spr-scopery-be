package com.company.scopery.modules.notification.emailtemplate.application.command;

import java.util.UUID;

public record PublishEmailTemplateVersionCommand(
        UUID templateId,
        UUID versionId
) {}
