package com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = SupportTableNames.KNOWLEDGE_LINK) @Getter @Setter @NoArgsConstructor
public class SupportKnowledgeLinkJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "support_case_id") private UUID supportCaseId;
    @Column(name = "problem_id") private UUID problemId;
    @Column(name = "incident_id") private UUID incidentId;
    @Column(name = "document_id") private UUID documentId;
    @Column(name = "document_version_id") private UUID documentVersionId;
    @Column(name = "link_type", nullable = false) private String linkType;
    @Column(name = "client_visible", nullable = false) private boolean clientVisible;
    @Version private Integer version;
}
