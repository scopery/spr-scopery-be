package com.company.scopery.modules.aicontext.policy.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aicontext.shared.constant.AiContextTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiContextTableNames.RESOLUTION_POLICY)
public class AiContextResolutionPolicyJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "policy_code", nullable = false, length = 100)
    private String policyCode;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "max_tokens", nullable = false)
    private int maxTokens = 8000;

    @Column(name = "include_related", nullable = false)
    private boolean includeRelated;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}
