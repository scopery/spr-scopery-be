package com.company.scopery.modules.specpack.outline.infrastructure.persistence;

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
        name = SpecPackTableNames.SPEC_PACK_OUTLINE,
        indexes = {
                @Index(name = "idx_spec_pack_outline_session_id", columnList = "session_id"),
                @Index(name = "idx_spec_pack_outline_status", columnList = "status"),
                @Index(name = "idx_spec_pack_outline_version", columnList = "session_id,version_number")
        }
)
public class SpecPackOutlineJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "outline_json", nullable = false, columnDefinition = "jsonb")
    private String outlineJson;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
