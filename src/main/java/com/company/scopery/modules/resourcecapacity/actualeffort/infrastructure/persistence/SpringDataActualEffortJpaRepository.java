package com.company.scopery.modules.resourcecapacity.actualeffort.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataActualEffortJpaRepository extends JpaRepository<ActualEffortRecordJpaEntity, UUID> {
    List<ActualEffortRecordJpaEntity> findByProjectId(UUID projectId);
}
