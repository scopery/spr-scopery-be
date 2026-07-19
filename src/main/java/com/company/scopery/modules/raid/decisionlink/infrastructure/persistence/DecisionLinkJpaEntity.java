package com.company.scopery.modules.raid.decisionlink.infrastructure.persistence;

import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = RaidTableNames.DECISION_LINK)
@Getter
@Setter
@NoArgsConstructor
public class DecisionLinkJpaEntity {
    @Id
    private UUID id;
    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "link_type", nullable = false)
    private String linkType;
    @Column(name = "target_type", nullable = false)
    private String targetType;
    @Column(name = "target_id", nullable = false)
    private UUID targetId;
    @Version
    private Integer version;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by")
    private String createdBy;
}
