package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record CreateFormCommand(UUID workspaceId, String formCode, String name, String objectType, String formType, UUID projectId) {}
