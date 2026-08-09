package com.company.scopery.modules.elicitation.session.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = ElicitationTableNames.SESSION,
        indexes = {
                @Index(name = "idx_elicitation_session_project", columnList = "project_id"),
                @Index(name = "idx_elicitation_session_scope", columnList = "scope_package_id"),
                @Index(name = "idx_elicitation_session_status", columnList = "status")
        }
)
public class ElicitationSessionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "scope_package_id", nullable = false, updatable = false)
    private UUID scopePackageId;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Override
    public UUID getId() { return id; }
}
