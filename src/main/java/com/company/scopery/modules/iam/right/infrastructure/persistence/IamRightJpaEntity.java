package com.company.scopery.modules.iam.right.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.iam.shared.constant.IamTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = IamTableNames.IAM_RIGHT,
        uniqueConstraints = @UniqueConstraint(name = "uq_iam_right_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_iam_right_code",   columnList = "code"),
                @Index(name = "idx_iam_right_module", columnList = "module"),
                @Index(name = "idx_iam_right_status", columnList = "status")
        }
)
public class IamRightJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "module", nullable = false, length = 100)
    private String module;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
