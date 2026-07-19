package com.company.scopery.modules.productivity.search.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.SEARCH_INDEX) @Getter @Setter @NoArgsConstructor
public class SearchIndexJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(name="target_id", nullable=false) private UUID targetId;
    @Column(nullable=false) private String title;
    private String subtitle; @Column(name="body_text", columnDefinition="text") private String bodyText;
    private String status; @Column(name="tags_json") private String tagsJson;
    @Column(name="visibility_class") private String visibilityClass;
    @Column(nullable=false) private boolean restricted;
    @Column(name="indexed_at", nullable=false) private Instant indexedAt;
    @Column(name="source_hash") private String sourceHash;
    @Version private Integer version;
}
