package com.company.scopery.modules.aiaction.application.query;

import com.company.scopery.modules.aiaction.request.domain.enums.AiActionRequestStatus;

import java.util.List;
import java.util.UUID;

public record SearchAiActionHistoryQuery(
        UUID workspaceId,
        UUID projectId,
        List<AiActionRequestStatus> statusList,
        int page,
        int size,
        UUID actorId
) {}
