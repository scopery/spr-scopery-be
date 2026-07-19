package com.company.scopery.modules.governance.ownership.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ObjectOwnershipRepository {
    ObjectOwnership save(ObjectOwnership o);
    Optional<ObjectOwnership> findActive(String objectTypeCode, UUID targetId);
    List<ObjectOwnership> findByProjectId(UUID projectId);
}
