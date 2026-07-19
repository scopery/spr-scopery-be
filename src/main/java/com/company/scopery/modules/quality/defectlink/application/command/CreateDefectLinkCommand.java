package com.company.scopery.modules.quality.defectlink.application.command;
import java.util.UUID;
public record CreateDefectLinkCommand(UUID projectId, UUID defectId, String targetType, UUID targetId, String linkType) {}
