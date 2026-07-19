package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record SubmitFormCommand(UUID workspaceId, UUID formId, UUID versionId, String objectType, UUID targetId, UUID projectId, String payloadJson) {}
