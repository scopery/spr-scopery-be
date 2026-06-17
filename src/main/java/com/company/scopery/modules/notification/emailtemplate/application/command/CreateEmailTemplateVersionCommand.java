package com.company.scopery.modules.notification.emailtemplate.application.command;

import java.util.UUID;

public record CreateEmailTemplateVersionCommand(
        UUID templateId,
        String subjectTemplate,
        String htmlBodyTemplate,
        String textBodyTemplate
) {}
