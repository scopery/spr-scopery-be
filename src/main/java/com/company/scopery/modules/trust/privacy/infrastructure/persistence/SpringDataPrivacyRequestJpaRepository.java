package com.company.scopery.modules.trust.privacy.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataPrivacyRequestJpaRepository extends JpaRepository<PrivacyRequestJpaEntity, UUID> {
    List<PrivacyRequestJpaEntity> findByWorkspaceId(UUID workspaceId);
}
