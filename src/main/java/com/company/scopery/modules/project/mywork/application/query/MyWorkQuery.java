package com.company.scopery.modules.project.mywork.application.query;

import com.company.scopery.modules.project.mywork.domain.enums.MyWorkWindow;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MyWorkQuery(
        UUID workspaceId,
        MyWorkWindow window,
        LocalDate dateFrom,
        LocalDate dateTo,
        List<String> statuses,
        UUID projectId,
        boolean includeCompleted,
        int page,
        int size
) {}
