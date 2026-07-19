package com.company.scopery.modules.clientportal.uat.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.UAT_ASSIGNMENT) @Getter @Setter @NoArgsConstructor
public class ClientUatAssignmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="test_case_id") private UUID testCaseId;
    @Column(name="test_run_id") private UUID testRunId;
    @Column(name="portal_account_id", nullable=false) private UUID portalAccountId;
    @Column(nullable=false) private String status;
    @Column(columnDefinition="text") private String notes;
    @Version private Integer version;
}
