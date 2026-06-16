package com.company.scopery.modules.workspace.team.application.query;

import java.util.UUID;

public record SearchTeamMemberQuery(UUID teamId, int page, int size) {
}
