package com.company.scopery.modules.specpack.version.infrastructure.persistence;

import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = SpecPackTableNames.SPEC_PACK_VERSION,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_spec_pack_version_number", columnNames = {"spec_pack_id", "version_number"})
        },
        indexes = {
                @Index(name = "idx_spec_pack_version_pack_id", columnList = "spec_pack_id")
        }
)
public class SpecPackVersionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "spec_pack_id", nullable = false, updatable = false)
    private UUID specPackId;

    @Column(name = "version_number", nullable = false, updatable = false)
    private int versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_json", nullable = false, columnDefinition = "jsonb")
    private String snapshotJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "outline_json", columnDefinition = "jsonb")
    private String outlineJson;

    @Column(name = "block_count", nullable = false)
    private int blockCount;

    @Column(name = "asset_count", nullable = false)
    private int assetCount;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
