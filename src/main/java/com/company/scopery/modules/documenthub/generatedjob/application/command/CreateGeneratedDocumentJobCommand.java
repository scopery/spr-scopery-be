package com.company.scopery.modules.documenthub.generatedjob.application.command;
import java.util.UUID;
public record CreateGeneratedDocumentJobCommand(UUID projectId, UUID templateId, UUID templateVersionId, String jobType, String sourceType, UUID sourceId) {}
