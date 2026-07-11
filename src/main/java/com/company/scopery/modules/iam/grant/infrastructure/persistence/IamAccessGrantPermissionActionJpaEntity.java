package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.modules.iam.shared.constant.IamTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = IamTableNames.IAM_ACCESS_GRANT_PERMISSION_ACTION,
        indexes = {
                @Index(name = "idx_iam_agpa_grant_id", columnList = "grant_id"),
                @Index(name = "idx_iam_agpa_permission_action_id", columnList = "permission_action_id")
        }
)
@IdClass(IamAccessGrantPermissionActionKey.class)
@EntityListeners(AuditingEntityListener.class)
public class IamAccessGrantPermissionActionJpaEntity {

    @Id
    @Column(name = "grant_id", nullable = false, updatable = false)
    private UUID grantId;

    @Id
    @Column(name = "permission_action_id", nullable = false, updatable = false)
    private UUID permissionActionId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;
}
