package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ConfigurationTableNames.STATUS_SET) @Getter @Setter @NoArgsConstructor
public class StatusSetJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="object_type_code", nullable=false) private String objectTypeCode;
    @Column(name="set_code", nullable=false) private String setCode;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
