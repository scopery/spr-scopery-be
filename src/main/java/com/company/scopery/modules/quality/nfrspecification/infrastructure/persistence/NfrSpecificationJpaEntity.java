package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.util.UUID;
@Entity @Table(name = QualityTableNames.NFR_SPECIFICATION) @Getter @Setter @NoArgsConstructor
public class NfrSpecificationJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "requirement_id") private UUID requirementId;
    @Column(name = "quality_attribute", nullable = false, length = 50) private String qualityAttribute;
    @Column(name = "metric_name", length = 100) private String metricName;
    @Column(name = "comparison_operator", length = 20) private String comparisonOperator;
    @Column(name = "target_value", precision = 20, scale = 4) private BigDecimal targetValue;
    @Column(name = "secondary_target_value", precision = 20, scale = 4) private BigDecimal secondaryTargetValue;
    @Column(length = 50) private String unit;
    @Column(name = "measurement_window", length = 100) private String measurementWindow;
    @Column(length = 100) private String environment;
    @Column(name = "verification_frequency", length = 50) private String verificationFrequency;
    @Column(name = "configuration_json", columnDefinition = "text") private String configurationJson;

    @Override public Object getId() { return requirementId; }
}
