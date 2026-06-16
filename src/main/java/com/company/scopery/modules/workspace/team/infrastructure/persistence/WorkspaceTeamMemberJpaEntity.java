package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.modules.workspace.shared.constant.WorkspaceTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = WorkspaceTableNames.TEAM_MEMBER,
        indexes = {
                @Index(name = "idx_workspace_team_member_team_id", columnList = "team_id"),
                @Index(name = "idx_workspace_team_member_user_id", columnList = "user_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@IdClass(WorkspaceTeamMemberKey.class)
public class WorkspaceTeamMemberJpaEntity {

    @Id
    @Column(name = "team_id", nullable = false, updatable = false)
    private UUID teamId;

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

}
