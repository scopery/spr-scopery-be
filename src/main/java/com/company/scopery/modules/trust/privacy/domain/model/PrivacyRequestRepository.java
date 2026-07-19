package com.company.scopery.modules.trust.privacy.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface PrivacyRequestRepository {
    PrivacyRequest save(PrivacyRequest r);
    Optional<PrivacyRequest> findById(UUID id);
    List<PrivacyRequest> findByWorkspaceId(UUID workspaceId);
}
