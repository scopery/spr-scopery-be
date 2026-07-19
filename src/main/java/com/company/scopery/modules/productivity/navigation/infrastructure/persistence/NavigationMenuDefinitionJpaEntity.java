package com.company.scopery.modules.productivity.navigation.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.NAVIGATION) @Getter @Setter @NoArgsConstructor
public class NavigationMenuDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(nullable=false, unique=true) private String code;
    @Column(name="parent_code") private String parentCode;
    @Column(nullable=false) private String label;
    @Column(name="menu_type") private String menuType;
    @Column(name="route_path") private String routePath;
    @Column(name="required_permission") private String requiredPermission;
    @Column(name="context_type") private String contextType;
    @Column(name="sort_order", nullable=false) private int sortOrder;
    @Column(nullable=false) private boolean enabled;
    @Version private Integer version;
}
