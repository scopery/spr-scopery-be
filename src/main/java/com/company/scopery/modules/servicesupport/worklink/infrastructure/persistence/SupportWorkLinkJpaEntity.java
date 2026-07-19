package com.company.scopery.modules.servicesupport.worklink.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.WORK_LINK) @Getter @Setter @NoArgsConstructor
public class SupportWorkLinkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "support_case_id", nullable = false) private UUID supportCaseId;
    @Column(name = "target_object_type", nullable = false) private String targetObjectType;
    @Column(name = "target_object_id", nullable = false) private UUID targetObjectId;
    @Column(name = "link_type", nullable = false) private String linkType;
    @Version private Integer version;
}
