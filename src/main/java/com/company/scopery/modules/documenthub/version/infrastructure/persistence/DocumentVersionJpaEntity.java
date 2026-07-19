package com.company.scopery.modules.documenthub.version.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = DocumentHubTableNames.VERSION) @Getter @Setter @NoArgsConstructor
public class DocumentVersionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="document_id", nullable=false) private UUID documentId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="version_number", nullable=false) private Integer versionNumber;
    @Column(name="storage_key", nullable=false) private String storageKey;
    @Column(name="file_name", nullable=false) private String fileName;
    @Column(name="content_type") private String contentType;
    @Column(name="file_size_bytes") private Long fileSizeBytes;
    @Column private String checksum;
    @Column(nullable=false) private String status;
    @Column(name="change_notes", columnDefinition="text") private String changeNotes;
    @Column(name="uploaded_by") private UUID uploadedBy;
    @Column(name="uploaded_at") private Instant uploadedAt;

    // Phase 41 — object storage tracking (V96 migration)
    @Column(name="storage_provider") private String storageProvider;
    @Column(name="storage_status", nullable=false) private String storageStatus;
    @Column(name="storage_etag") private String storageEtag;
    @Column(name="upload_completed_at") private Instant uploadCompletedAt;
    @Column(name="storage_verified_at") private Instant storageVerifiedAt;

    @Version private Integer version;
}
