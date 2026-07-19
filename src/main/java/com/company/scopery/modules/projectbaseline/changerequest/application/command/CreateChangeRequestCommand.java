package com.company.scopery.modules.projectbaseline.changerequest.application.command;
import java.util.UUID;
public record CreateChangeRequestCommand(UUID projectId, String code, String title, String description,
        String changeType, String priority, UUID baselineId, String reason) {}
