package com.company.scopery.modules.servicesupport.effort.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;

@Entity @Table(name = SupportTableNames.EFFORT_RECORD)
@Getter @Setter @NoArgsConstructor
public class SupportEffortRecordJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="support_case_id", nullable=false) private UUID supportCaseId;
    @Column(name="resource_profile_id") private UUID resourceProfileId;
    @Column(name="effort_hours", nullable=false) private BigDecimal effortHours;
    @Column(name="effort_date", nullable=false) private LocalDate effortDate;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
