package com.company.scopery.modules.projectbaseline.changerequest.application.command;
import java.util.UUID;
public record UpdateChangeRequestCommand(UUID projectId, UUID changeRequestId, String title, String description,
        String changeType, String priority, UUID baselineId, String reason) {}
