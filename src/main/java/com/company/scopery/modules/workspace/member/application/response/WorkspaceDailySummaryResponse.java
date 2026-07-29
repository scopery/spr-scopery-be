package com.company.scopery.modules.workspace.member.application.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WorkspaceDailySummaryResponse(
        UUID workspaceId,
        LocalDate date,
        List<DailySummaryMemberEntry> members) {
}
