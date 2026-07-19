package com.company.scopery.modules.governance.grant.domain.model;
import java.util.*; import java.util.UUID;
public interface ObjectAccessGrantRepository {
    ObjectAccessGrant save(ObjectAccessGrant g);
    Optional<ObjectAccessGrant> findById(UUID id);
    List<ObjectAccessGrant> findActiveByTarget(String objectTypeCode, UUID targetId);
    List<ObjectAccessGrant> findByProjectId(UUID projectId);
}
