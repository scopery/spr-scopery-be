package com.company.scopery.modules.trust.consent.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ContactSuppressionRepository {
    ContactSuppression save(ContactSuppression s);
    Optional<ContactSuppression> findById(UUID id);
    List<ContactSuppression> findByWorkspaceId(UUID workspaceId);
}
