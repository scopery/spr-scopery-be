package com.company.scopery.modules.servicesupport.assignment.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = SupportTableNames.ASSIGNMENT)
@Getter @Setter @NoArgsConstructor
public class SupportAssignmentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "support_case_id", nullable = false) private UUID supportCaseId;
    @Column(name = "assignment_type", nullable = false) private String assignmentType;
    @Column(name = "assignee_user_id") private UUID assigneeUserId;
    @Column(name = "resource_profile_id") private UUID resourceProfileId;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}
