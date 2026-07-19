package com.company.scopery.modules.servicesupport.snapshot.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportMetricSnapshotJpaRepository extends JpaRepository<SupportMetricSnapshotJpaEntity, UUID> {
    List<SupportMetricSnapshotJpaEntity> findByWorkspaceId(UUID workspaceId);
}
