package com.company.scopery.modules.workspace.member.application.response;

import java.util.List;
import java.util.UUID;

public record DailySummaryMemberEntry(
        UUID userId,
        List<DailySummaryTaskItem> done,
        List<DailySummaryTaskItem> inProgress) {
}
