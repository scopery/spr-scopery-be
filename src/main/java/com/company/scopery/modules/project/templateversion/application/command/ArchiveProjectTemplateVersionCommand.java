package com.company.scopery.modules.project.templateversion.application.command;

import java.util.UUID;

public record ArchiveProjectTemplateVersionCommand(UUID templateId, UUID versionId) {}
