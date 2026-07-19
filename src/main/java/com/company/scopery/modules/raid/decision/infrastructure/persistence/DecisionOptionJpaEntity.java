package com.company.scopery.modules.raid.decision.infrastructure.persistence;
import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name=RaidTableNames.OPTION) @Getter @Setter @NoArgsConstructor
public class DecisionOptionJpaEntity {
    @Id private UUID id;
    @Column(name="decision_id", nullable=false) private UUID decisionId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="option_title", nullable=false) private String optionTitle;
    @Column(name="option_description", columnDefinition="text") private String optionDescription;
    @Column(columnDefinition="text") private String pros;
    @Column(columnDefinition="text") private String cons;
    @Column(name="estimated_impact", columnDefinition="text") private String estimatedImpact;
    @Column(name="selected_flag", nullable=false) private boolean selectedFlag;
    private Integer version;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="created_by") private String createdBy;
}
