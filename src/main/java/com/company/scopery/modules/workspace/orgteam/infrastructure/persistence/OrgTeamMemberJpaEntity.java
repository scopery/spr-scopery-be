package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import com.company.scopery.modules.workspace.shared.constant.WorkspaceTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = WorkspaceTableNames.ORG_TEAM_MEMBER,
        indexes = {
                @Index(name = "idx_org_team_member_team_id", columnList = "team_id"),
                @Index(name = "idx_org_team_member_user_id", columnList = "user_id")
        }
)
public class OrgTeamMemberJpaEntity {

    @EmbeddedId
    private OrgTeamMemberKey id;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}
