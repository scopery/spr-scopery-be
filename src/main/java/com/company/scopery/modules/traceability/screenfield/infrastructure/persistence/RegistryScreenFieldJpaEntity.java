package com.company.scopery.modules.traceability.screenfield.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.SCREEN_FIELD) @Getter @Setter @NoArgsConstructor
public class RegistryScreenFieldJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="screen_id", nullable=false) private UUID screenId;
    @Column(name="section_id") private UUID sectionId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="field_key", nullable=false) private String fieldKey;
    @Column(nullable=false) private String label;
    @Column(name="field_type", nullable=false) private String fieldType;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private boolean required;
    @Column(name="display_order", nullable=false) private int displayOrder;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
