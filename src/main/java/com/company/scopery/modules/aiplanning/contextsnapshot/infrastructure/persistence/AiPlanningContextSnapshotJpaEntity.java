package com.company.scopery.modules.aiplanning.contextsnapshot.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = AiPlanningTableNames.CONTEXT_SNAPSHOT)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class AiPlanningContextSnapshotJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "actor_user_id", nullable = false)
    private UUID actorUserId;
    @Column(name = "context_type", nullable = false)
    private String contextType;
    @Column(name = "access_scope_json", nullable = false, columnDefinition = "text")
    private String accessScopeJson;
    @Column(name = "included_sections_json", nullable = false, columnDefinition = "text")
    private String includedSectionsJson;
    @Column(name = "redaction_summary_json", columnDefinition = "text")
    private String redactionSummaryJson;
    @Column(name = "context_payload_json", nullable = false, columnDefinition = "text")
    private String contextPayloadJson;
    @Column(name = "token_estimate")
    private Integer tokenEstimate;
    @Column(name = "trace_id")
    private String traceId;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by")
    private String createdBy;
}
