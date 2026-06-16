package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.modules.iam.shared.constant.IamTableNames;
import jakarta.persistence.*;
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
        name = IamTableNames.IAM_ACCESS_GRANT_RIGHT,
        indexes = {
                @Index(name = "idx_iam_agr_grant_id", columnList = "grant_id"),
                @Index(name = "idx_iam_agr_right_id", columnList = "right_id")
        }
)
@IdClass(IamAccessGrantRightKey.class)
@EntityListeners(AuditingEntityListener.class)
public class IamAccessGrantRightJpaEntity {

    @Id
    @Column(name = "grant_id", nullable = false, updatable = false)
    private UUID grantId;

    @Id
    @Column(name = "right_id", nullable = false, updatable = false)
    private UUID rightId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;
}
