package com.company.scopery.modules.integrationhub.credential.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataCredentialReferenceJpaRepository extends JpaRepository<CredentialReferenceJpaEntity, UUID> {
    List<CredentialReferenceJpaEntity> findByWorkspaceId(UUID workspaceId);
}
