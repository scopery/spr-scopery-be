package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.STATUS_VALUE) @Getter @Setter @NoArgsConstructor
public class StatusValueJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="status_set_id", nullable=false) private UUID statusSetId;
    @Column(name="value_code", nullable=false) private String valueCode;
    @Column(nullable=false) private String label;
    @Column(name="domain_category", nullable=false) private String domainCategory;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
