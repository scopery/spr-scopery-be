package com.company.scopery.modules.raid.raidlink.infrastructure.persistence;

import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = RaidTableNames.LINK)
@Getter
@Setter
@NoArgsConstructor
public class RaidLinkJpaEntity {
    @Id
    private UUID id;
    @Column(name = "raid_item_id", nullable = false)
    private UUID raidItemId;
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
