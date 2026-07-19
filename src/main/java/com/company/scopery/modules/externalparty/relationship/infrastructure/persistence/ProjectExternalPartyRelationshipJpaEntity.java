package com.company.scopery.modules.externalparty.relationship.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = ExternalPartyTableNames.RELATIONSHIP) @Getter @Setter @NoArgsConstructor
public class ProjectExternalPartyRelationshipJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="organization_id", nullable=false) private UUID organizationId;
    @Column(name="relationship_type", nullable=false) private String relationshipType;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String notes;
    @Version private Integer version;
}
