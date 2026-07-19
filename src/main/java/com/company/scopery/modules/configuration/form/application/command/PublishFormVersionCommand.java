package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record PublishFormVersionCommand(UUID workspaceId, UUID formId, UUID versionId) {}
