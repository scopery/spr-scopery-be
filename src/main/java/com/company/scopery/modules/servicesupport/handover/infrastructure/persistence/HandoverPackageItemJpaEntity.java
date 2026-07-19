package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.HANDOVER_ITEM) @Getter @Setter @NoArgsConstructor
public class HandoverPackageItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "handover_package_id", nullable = false) private UUID handoverPackageId;
    @Column(name = "item_type", nullable = false) private String itemType;
    @Column(name = "target_object_type") private String targetObjectType;
    @Column(name = "target_object_id") private UUID targetObjectId;
    @Column(name = "document_id") private UUID documentId;
    @Column(nullable = false) private String title;
    @Column private String description;
    @Column(name = "client_visible", nullable = false) private boolean clientVisible;
    @Column(name = "sort_order", nullable = false) private int sortOrder;
    @Version private Integer version;
}
