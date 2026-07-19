package com.company.scopery.modules.raid.raidaction.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.raid.shared.constant.RaidTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name=RaidTableNames.ACTION) @Getter @Setter @NoArgsConstructor
public class RaidActionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="raid_item_id", nullable=false) private UUID raidItemId;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(nullable=false) private String title;
    @Column(columnDefinition="text") private String description;
    @Column(name="owner_user_id") private UUID ownerUserId;
    @Column(name="due_date") private LocalDate dueDate;
    @Column(nullable=false) private String status;
    @Column(name="linked_task_id") private UUID linkedTaskId;
    @Column(name="completion_note", columnDefinition="text") private String completionNote;
    @Column(name="completed_at") private Instant completedAt;
    @Version private Integer version;
}
