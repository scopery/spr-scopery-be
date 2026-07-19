package com.company.scopery.modules.productivity.savedsearch.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.SAVED_SEARCH) @Getter @Setter @NoArgsConstructor
public class SavedSearchJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="owner_user_id", nullable=false) private UUID ownerUserId;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String scope;
    @Column(name="query_text", columnDefinition="text") private String queryText;
    @Column(name="filters_json", columnDefinition="text") private String filtersJson;
    @Column(name="sort_json", columnDefinition="text") private String sortJson;
    @Column(nullable=false) private String visibility;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
