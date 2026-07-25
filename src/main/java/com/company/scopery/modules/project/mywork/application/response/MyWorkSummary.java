package com.company.scopery.modules.project.mywork.application.response;

public record MyWorkSummary(
        long total,
        long overdue,
        long dueThisWindow,
        long inProgress,
        long todo,
        long blocked,
        long undated
) {}
