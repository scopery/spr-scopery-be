package com.company.scopery.modules.configuration.taxonomy.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.TAXONOMY_TERM) @Getter @Setter @NoArgsConstructor
public class TaxonomyTermJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="taxonomy_id", nullable=false) private UUID taxonomyId;
    @Column(name="parent_term_id") private UUID parentTermId;
    @Column(name="term_code", nullable=false) private String termCode;
    @Column(nullable=false) private String label;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
