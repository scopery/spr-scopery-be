package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record CreateFormVersionCommand(UUID workspaceId, UUID formId) {}
