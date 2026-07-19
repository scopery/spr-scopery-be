package com.company.scopery.modules.notification.advanced.digest.domain.model;
import java.util.*; import java.util.UUID;
public interface DigestRuleRepository {
    DigestRule save(DigestRule r);
    Optional<DigestRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DigestRule> findByWorkspaceId(UUID workspaceId);
    List<DigestRule> findAllActive();
}
