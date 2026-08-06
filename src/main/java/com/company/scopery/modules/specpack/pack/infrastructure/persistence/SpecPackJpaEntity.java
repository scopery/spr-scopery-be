package com.company.scopery.modules.specpack.pack.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = SpecPackTableNames.SPEC_PACK,
        indexes = {
                @Index(name = "idx_spec_pack_project_id", columnList = "project_id"),
                @Index(name = "idx_spec_pack_status",     columnList = "status"),
                @Index(name = "idx_spec_pack_pack_type",  columnList = "pack_type")
        }
)
public class SpecPackJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "pack_type", nullable = false, length = 50)
    private String packType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_version_id")
    private UUID currentVersionId;

    @Column(name = "source_pack_id")
    private UUID sourcePackId;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Override
    public UUID getId() { return id; }
}
