package com.company.scopery.modules.project.projectphase.application.query;

import java.util.UUID;

public record SearchProjectPhaseQuery(
        UUID projectId,
        String status,
        int page,
        int size
) {}
