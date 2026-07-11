package com.company.scopery.modules.notification.emailtrigger.domain.model;

import java.util.Map;
import java.util.UUID;

public record EmailNotificationTriggerPayload(
        UUID eventDefinitionId,
        String sourceSystem,
        String eventKey,
        UUID workspaceId,
        UUID actorUserId,
        Map<String, Object> payload
) {}
