package com.company.scopery.modules.ratecard.membercostrole.application.query;
import java.time.LocalDate; import java.util.UUID;
public record SearchMemberCostRoleQuery(UUID workspaceId, UUID workspaceMemberId, UUID userId, UUID costRoleId,
        String status, LocalDate effectiveDate, int page, int size) {}
