package com.company.scopery.modules.ratecard.ratecardversion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.ratecard.shared.constant.RateCardTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = RateCardTableNames.RATE_CARD_VERSION,
        uniqueConstraints = @UniqueConstraint(name = "uq_rate_card_version_card_number",
                columnNames = {"rate_card_id", "version_number"}),
        indexes = {
                @Index(name = "idx_rate_card_version_card", columnList = "rate_card_id"),
                @Index(name = "idx_rate_card_version_status", columnList = "status")
        })
public class RateCardVersionJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "rate_card_id", nullable = false, updatable = false) private UUID rateCardId;
    @Column(name = "version_number", nullable = false) private Integer versionNumber;
    @Column(name = "name", length = 255) private String name;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Column(name = "effective_from", nullable = false) private LocalDate effectiveFrom;
    @Column(name = "effective_to") private LocalDate effectiveTo;
    @Column(name = "status", nullable = false, length = 50) private String status;
    @Column(name = "published_at") private Instant publishedAt;
    @Column(name = "published_by") private UUID publishedBy;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Version @Column(name = "version", nullable = false) private Integer version;
}
