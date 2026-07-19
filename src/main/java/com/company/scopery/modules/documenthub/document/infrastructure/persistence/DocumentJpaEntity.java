package com.company.scopery.modules.documenthub.document.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.DOCUMENT)
@Getter @Setter @NoArgsConstructor
public class DocumentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "folder_id") private UUID folderId;
    @Column(name = "document_type_code") private String documentTypeCode;
    private String code;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(nullable = false) private String status;
    private String classification;
    @Column(name = "current_version_id") private UUID currentVersionId;
    @Column(nullable = false) private boolean locked;
    @Column(name = "approved_at") private Instant approvedAt;
    @Column(name = "approved_by") private UUID approvedBy;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Column(name = "trace_id") private String traceId;
    @Version private Integer version;
    // Native editor fields
    @Column(name = "content_mode", nullable = false) private String contentMode;
    @Column(name = "parent_document_id") private UUID parentDocumentId;
    @Column(name = "current_content_revision_id") private UUID currentContentRevisionId;
    @Column(name = "current_content_revision_no", nullable = false) private long currentContentRevisionNo;
    @Column(name = "editor_schema_version") private Integer editorSchemaVersion;
    @Column(name = "content_checksum") private String contentChecksum;
    @Column(name = "content_updated_at") private Instant contentUpdatedAt;
    @Column(name = "content_updated_by") private UUID contentUpdatedBy;
    @Column(name = "template_version_id") private UUID templateVersionId;
    @Column(name = "page_icon") private String pageIcon;
    @Column(name = "page_cover_object_key") private String pageCoverObjectKey;
    @Column(name = "content_width", nullable = false) private String contentWidth;
    @Column(name = "client_visible", nullable = false) private boolean clientVisible;
}
