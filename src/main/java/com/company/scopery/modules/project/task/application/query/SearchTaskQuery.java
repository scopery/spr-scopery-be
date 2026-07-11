package com.company.scopery.modules.project.task.application.query;

import java.util.UUID;

public record SearchTaskQuery(
        UUID projectId,
        UUID projectPhaseId,
        UUID wbsNodeId,
        String status,
        String priority,
        String keyword,
        int page,
        int size
) {}
