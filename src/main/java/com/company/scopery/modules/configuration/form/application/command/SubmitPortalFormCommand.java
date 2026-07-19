package com.company.scopery.modules.configuration.form.application.command;
import java.util.UUID;
public record SubmitPortalFormCommand(UUID projectId, UUID formId, UUID versionId, String objectType, UUID targetId, String payloadJson) {}
