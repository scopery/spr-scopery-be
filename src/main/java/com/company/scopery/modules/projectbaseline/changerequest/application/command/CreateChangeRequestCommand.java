package com.company.scopery.modules.projectbaseline.changerequest.application.command;

import java.util.UUID;

public record CreateChangeRequestCommand(
        UUID projectId, String code, String title, String description,
        String changeType, String priority, UUID baselineId, String reason,
        // source provenance — null means MANUAL
        String sourceType, UUID sourceId, String sourceSubtype, String sourceCode, String sourceTitle
) {
    public CreateChangeRequestCommand(UUID projectId, String code, String title, String description,
                                      String changeType, String priority, UUID baselineId, String reason) {
        this(projectId, code, title, description, changeType, priority, baselineId, reason,
                null, null, null, null, null);
    }
}
