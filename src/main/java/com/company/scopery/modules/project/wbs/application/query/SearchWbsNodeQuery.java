package com.company.scopery.modules.project.wbs.application.query;

import java.util.UUID;

public record SearchWbsNodeQuery(
        UUID projectId,
        UUID phaseId,
        UUID parentId,
        String status,
        int page,
        int size
) {}
