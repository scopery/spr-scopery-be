package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataHandoverPackageItemJpaRepository extends JpaRepository<HandoverPackageItemJpaEntity, UUID> {
    List<HandoverPackageItemJpaEntity> findByHandoverPackageId(UUID handoverPackageId);
}
