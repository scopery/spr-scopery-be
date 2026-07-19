package com.company.scopery.modules.servicesupport.warranty.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.servicesupport.shared.constant.SupportTableNames;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.LocalDate; import java.util.UUID;

@Entity @Table(name = SupportTableNames.WARRANTY_COVERAGE)
@Getter @Setter @NoArgsConstructor
public class WarrantyCoverageJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(name="service_profile_id") private UUID serviceProfileId;
    @Column(name="start_date", nullable=false) private LocalDate startDate;
    @Column(name="end_date") private LocalDate endDate;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}
