package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataServiceHandoverPackageJpaRepository extends JpaRepository<ServiceHandoverPackageJpaEntity, UUID> {
    List<ServiceHandoverPackageJpaEntity> findByWorkspaceId(UUID workspaceId);
}
