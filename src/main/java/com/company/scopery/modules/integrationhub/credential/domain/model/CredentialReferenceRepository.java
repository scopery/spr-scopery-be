package com.company.scopery.modules.integrationhub.credential.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface CredentialReferenceRepository {
    CredentialReference save(CredentialReference c);
    Optional<CredentialReference> findById(UUID id);
    List<CredentialReference> findByWorkspaceId(UUID workspaceId);
}
