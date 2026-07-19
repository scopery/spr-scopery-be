package com.company.scopery.modules.governance.objecttype.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.governance.shared.constant.GovernanceTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = GovernanceTableNames.OBJECT_TYPE) @Getter @Setter @NoArgsConstructor
public class GovernedObjectTypeJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "object_type_code", nullable = false, unique = true) private String objectTypeCode;
    @Column(name = "ownership_supported", nullable = false) private boolean ownershipSupported;
    @Column(name = "versioning_supported", nullable = false) private boolean versioningSupported;
    @Column(name = "locking_supported", nullable = false) private boolean lockingSupported;
    @Column(name = "restore_supported", nullable = false) private boolean restoreSupported;
    @Column(name = "enabled", nullable = false) private boolean enabled;
    @Version private Integer version;
}
