package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMember;

import java.time.Instant;
import java.util.UUID;

public record OrgTeamMemberResponse(
        UUID teamId,
        UUID userId,
        Instant joinedAt) {

    public static OrgTeamMemberResponse from(OrgTeamMember member) {
        return new OrgTeamMemberResponse(member.teamId(), member.userId(), member.joinedAt());
    }
}
