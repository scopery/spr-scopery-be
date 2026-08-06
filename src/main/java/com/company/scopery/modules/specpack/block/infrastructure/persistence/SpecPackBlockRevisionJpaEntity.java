package com.company.scopery.modules.specpack.block.infrastructure.persistence;

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
        name = SpecPackTableNames.SPEC_PACK_BLOCK_REVISION,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_spec_pack_block_revision_number", columnNames = {"spec_pack_block_id", "revision_number"})
        },
        indexes = {
                @Index(name = "idx_spec_pack_block_revision_block_id", columnList = "spec_pack_block_id")
        }
)
public class SpecPackBlockRevisionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "spec_pack_block_id", nullable = false, updatable = false)
    private UUID specPackBlockId;

    @Column(name = "revision_number", nullable = false, updatable = false)
    private int revisionNumber;

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

    @Column(name = "change_source", nullable = false, length = 50)
    private String changeSource;

    @Column(name = "change_comment", columnDefinition = "TEXT")
    private String changeComment;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
