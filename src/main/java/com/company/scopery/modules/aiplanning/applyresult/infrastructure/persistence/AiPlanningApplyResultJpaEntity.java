package com.company.scopery.modules.aiplanning.applyresult.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = AiPlanningTableNames.APPLY_RESULT) @Getter @Setter @NoArgsConstructor
public class AiPlanningApplyResultJpaEntity {
    @Id private UUID id;
    @Column(name = "suggestion_id", nullable = false) private UUID suggestionId;
    @Column(name = "suggestion_item_id") private UUID suggestionItemId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(nullable = false) private String status;
    @Column(name = "domain_action") private String domainAction;
    @Column(name = "target_type") private String targetType;
    @Column(name = "target_id") private UUID targetId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_payload_json", columnDefinition = "jsonb") private String resultPayloadJson;
    @Column(name = "error_code") private String errorCode;
    @Column(name = "error_message", columnDefinition = "text") private String errorMessage;
    @Column(name = "created_by") private UUID createdBy;
    @Column(name = "trace_id") private String traceId;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
}
