package com.company.scopery.modules.trust.legalhold.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface LegalHoldRepository {
    LegalHold save(LegalHold h);
    Optional<LegalHold> findById(UUID id);
    List<LegalHold> findByWorkspaceId(UUID workspaceId);
    List<LegalHold> findActiveByWorkspaceId(UUID workspaceId);
}
