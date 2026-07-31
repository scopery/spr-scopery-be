package com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence;
import com.company.scopery.modules.quality.shared.constant.QualityTableNames;
import jakarta.persistence.*;
import lombok.*; import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = QualityTableNames.NFR_TARGET)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class NfrTargetJpaEntity implements Persistable<UUID> {
    @Id private UUID id;
    @Column(name = "requirement_id", nullable = false) private UUID requirementId;
    @Column(name = "target_type", nullable = false, length = 50) private String targetType;
    @Column(name = "target_id") private UUID targetId;
    @Column(name = "target_label", length = 255) private String targetLabel;
    @Column(name = "display_order", nullable = false) private int displayOrder;
    @CreatedDate @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @Override public UUID getId() { return id; }
    @Override public boolean isNew() { return createdAt == null; }
}
