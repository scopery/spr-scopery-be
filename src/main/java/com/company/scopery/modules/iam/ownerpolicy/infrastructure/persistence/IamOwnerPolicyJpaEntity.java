package com.company.scopery.modules.iam.ownerpolicy.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.iam.shared.constant.IamTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = IamTableNames.IAM_OWNER_POLICY)
public class IamOwnerPolicyJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;
    @Column(name = "policy_version", nullable = false)
    private Integer policyVersion;
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_bundle", nullable = false, columnDefinition = "jsonb")
    private String actionBundle;
    @Column(name = "inheritance_scope", nullable = false, length = 50)
    private String inheritanceScope;
    @Column(name = "can_delegate", nullable = false)
    private boolean canDelegate;
    @Column(name = "delegation_depth", nullable = false)
    private int delegationDepth;
    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;
    @Column(name = "effective_to")
    private Instant effectiveTo;
    @Version @Column(name = "version", nullable = false)
    private Integer version;
}
