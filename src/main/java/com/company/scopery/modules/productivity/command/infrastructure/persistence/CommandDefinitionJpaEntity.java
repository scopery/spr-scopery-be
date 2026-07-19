package com.company.scopery.modules.productivity.command.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.productivity.shared.constant.ProductivityTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.util.UUID;
@Entity @Table(name = ProductivityTableNames.COMMAND) @Getter @Setter @NoArgsConstructor
public class CommandDefinitionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(nullable=false, unique=true) private String code;
    @Column(nullable=false) private String title;
    private String category;
    @Column(name="required_permission") private String requiredPermission;
    @Column(nullable=false) private boolean dangerous;
    @Column(name="confirmation_required", nullable=false) private boolean confirmationRequired;
    @Column(nullable=false) private boolean enabled;
    @Version private Integer version;
}
