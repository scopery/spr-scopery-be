package com.company.scopery.modules.documenthub.generatedjob.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = DocumentHubTableNames.GENERATED_JOB) @Getter @Setter @NoArgsConstructor
public class GeneratedDocumentJobJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="template_id") private UUID templateId;
    @Column(name="template_version_id") private UUID templateVersionId;
    @Column(name="job_type", nullable=false) private String jobType;
    @Column(nullable=false) private String status;
    @Column(name="source_type") private String sourceType;
    @Column(name="source_id") private UUID sourceId;
    @Column(name="output_document_id") private UUID outputDocumentId;
    @Column(name="error_message", columnDefinition="text") private String errorMessage;
    @Column(name="requested_by") private UUID requestedBy;
    @Column(name="started_at") private Instant startedAt;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
