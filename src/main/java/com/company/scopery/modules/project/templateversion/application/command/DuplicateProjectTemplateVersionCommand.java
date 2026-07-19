package com.company.scopery.modules.project.templateversion.application.command;

import java.util.UUID;

public record DuplicateProjectTemplateVersionCommand(UUID templateId, UUID versionId) {}
