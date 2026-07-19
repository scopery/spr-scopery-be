package com.company.scopery.modules.trust.consent.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ConsentRecordRepository {
    ConsentRecord save(ConsentRecord r);
    Optional<ConsentRecord> findById(UUID id);
    List<ConsentRecord> findByWorkspaceId(UUID workspaceId);
}
