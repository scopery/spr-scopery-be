package com.company.scopery.modules.workspace.member.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record WorkspaceDailySummaryQuery(
        UUID workspaceId,
        LocalDate date) {
}
