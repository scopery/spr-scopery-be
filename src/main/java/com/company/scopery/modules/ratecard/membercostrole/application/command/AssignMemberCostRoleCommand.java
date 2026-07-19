package com.company.scopery.modules.ratecard.membercostrole.application.command;
import java.time.LocalDate; import java.util.UUID;
public record AssignMemberCostRoleCommand(UUID workspaceId, UUID workspaceMemberId, UUID costRoleId,
        Boolean isDefault, LocalDate effectiveFrom, LocalDate effectiveTo) {}
