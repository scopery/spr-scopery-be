package com.company.scopery.modules.productivity.savedview.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.SAVED_VIEW) @Getter @Setter @NoArgsConstructor
public class SavedViewJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="owner_user_id", nullable=false) private UUID ownerUserId;
    @Column(name="target_type", nullable=false) private String targetType;
    @Column(nullable=false) private String name;
    @Column(name="view_config_json") private String viewConfigJson;
    @Column(name="filters_json") private String filtersJson;
    @Column(name="sort_json") private String sortJson;
    @Column(name="columns_json") private String columnsJson;
    @Column(name="display_mode") private String displayMode;
    @Column(nullable=false) private String visibility;
    @Column(name="default_flag", nullable=false) private boolean defaultFlag;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
