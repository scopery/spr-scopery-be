package com.company.scopery.modules.traceability.tracelink.application.command;
import java.util.UUID;
public record CreateTraceLinkCommand(UUID projectId, String sourceType, UUID sourceId, String targetType, UUID targetId, String linkType) {}
