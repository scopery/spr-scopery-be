package com.company.scopery.modules.ratecard.ratecardline.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.ratecard.shared.constant.RateCardTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = RateCardTableNames.RATE_CARD_LINE, indexes = {
        @Index(name = "idx_rate_card_line_version", columnList = "rate_card_version_id"),
        @Index(name = "idx_rate_card_line_role", columnList = "cost_role_id")
})
public class RateCardLineJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "rate_card_version_id", nullable = false, updatable = false) private UUID rateCardVersionId;
    @Column(name = "cost_role_id", nullable = false) private UUID costRoleId;
    @Column(name = "seniority_level", length = 100) private String seniorityLevel;
    @Column(name = "location_code", length = 100) private String locationCode;
    @Column(name = "currency_code", nullable = false, length = 10) private String currencyCode;
    @Column(name = "cost_rate_per_hour", nullable = false, precision = 18, scale = 4) private BigDecimal costRatePerHour;
    @Column(name = "billing_rate_per_hour", precision = 18, scale = 4) private BigDecimal billingRatePerHour;
    @Column(name = "notes", columnDefinition = "TEXT") private String notes;
    @Version @Column(name = "version", nullable = false) private Integer version;
}
