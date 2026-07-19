package com.company.scopery.modules.documenthub.folder.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = DocumentHubTableNames.FOLDER) @Getter @Setter @NoArgsConstructor
public class DocumentFolderJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="parent_folder_id") private UUID parentFolderId;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(nullable=false) private String status;
    @Column(name="sort_order") private Integer sortOrder;
    @Column(name="archived_at") private Instant archivedAt;
    @Column(name="archived_by") private UUID archivedBy;
    @Version private Integer version;
}
