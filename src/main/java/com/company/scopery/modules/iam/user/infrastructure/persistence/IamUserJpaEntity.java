package com.company.scopery.modules.iam.user.infrastructure.persistence;

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
        name = IamTableNames.IAM_USER,
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_iam_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_iam_user_email",    columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_iam_user_username", columnList = "username"),
                @Index(name = "idx_iam_user_email",    columnList = "email"),
                @Index(name = "idx_iam_user_status",   columnList = "status")
        }
)
public class IamUserJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
