package com.company.scopery.modules.servicesupport.requesttype.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportRequestTypeJpaRepository extends JpaRepository<SupportRequestTypeJpaEntity, UUID> {
    List<SupportRequestTypeJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndTypeCode(UUID workspaceId, String typeCode);
}
