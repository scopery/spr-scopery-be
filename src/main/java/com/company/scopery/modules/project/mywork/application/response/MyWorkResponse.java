package com.company.scopery.modules.project.mywork.application.response;

import com.company.scopery.modules.project.mywork.domain.enums.MyWorkWindow;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MyWorkResponse(
        UUID workspaceId,
        UUID userId,
        MyWorkWindow window,
        LocalDate dateFrom,
        LocalDate dateTo,
        MyWorkSummary summary,
        List<MyWorkTaskItem> items,
        MyWorkPageInfo page
) {
    public record MyWorkPageInfo(int page, int size, long totalElements, int totalPages) {}
}
