package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataRestoreRequestJpaRepository extends JpaRepository<RestoreRequestJpaEntity, UUID> {
    List<RestoreRequestJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
