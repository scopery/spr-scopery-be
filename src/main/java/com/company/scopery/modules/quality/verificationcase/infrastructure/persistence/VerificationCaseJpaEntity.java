package com.company.scopery.modules.quality.verificationcase.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.VERIFICATION_CASE) @Getter @Setter @NoArgsConstructor
public class VerificationCaseJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "requirement_id", nullable = false) private UUID requirementId;
    private String code;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "verification_method", nullable = false, length = 50) private String verificationMethod;
    @Column(columnDefinition = "text") private String procedure;
    @Column(name = "expected_result_json", columnDefinition = "text") private String expectedResultJson;
    @Column(length = 100) private String environment;
    @Column(name = "lifecycle_status", nullable = false, length = 50) private String lifecycleStatus;
    @Column(name = "automation_status", nullable = false, length = 20) private String automationStatus;
    @Column(name = "owner_id") private UUID ownerId;
    @Column(name = "assignee_id") private UUID assigneeId;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Version private Integer version;
}
