package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record WorkspaceTeamMemberKey(UUID teamId, UUID userId) implements Serializable {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceTeamMemberKey other)) return false;
        return Objects.equals(teamId, other.teamId) && Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, userId);
    }
}
