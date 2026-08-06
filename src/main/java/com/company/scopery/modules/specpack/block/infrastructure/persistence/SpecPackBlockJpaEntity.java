package com.company.scopery.modules.specpack.block.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.specpack.shared.constant.SpecPackTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = SpecPackTableNames.SPEC_PACK_BLOCK,
        indexes = {
                @Index(name = "idx_spec_pack_block_pack_id",       columnList = "spec_pack_id"),
                @Index(name = "idx_spec_pack_block_parent_id",     columnList = "parent_block_id"),
                @Index(name = "idx_spec_pack_block_block_type",    columnList = "block_type"),
                @Index(name = "idx_spec_pack_block_display_order", columnList = "spec_pack_id,display_order")
        }
)
public class SpecPackBlockJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "spec_pack_id", nullable = false, updatable = false)
    private UUID specPackId;

    @Column(name = "block_key", nullable = false, length = 500)
    private String blockKey;

    @Column(name = "parent_block_id")
    private UUID parentBlockId;

    @Column(name = "block_type", nullable = false, length = 50)
    private String blockType;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "content_format", nullable = false, length = 50)
    private String contentFormat;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_json", nullable = false, columnDefinition = "jsonb")
    private String contentJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_refs_json", columnDefinition = "jsonb")
    private String sourceRefsJson;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_revision_number", nullable = false)
    private int currentRevisionNumber;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Override
    public UUID getId() { return id; }
}
