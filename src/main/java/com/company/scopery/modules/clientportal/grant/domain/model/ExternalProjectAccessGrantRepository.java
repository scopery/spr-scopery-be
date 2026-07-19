package com.company.scopery.modules.clientportal.grant.domain.model;
import java.util.*;
public interface ExternalProjectAccessGrantRepository {
    ExternalProjectAccessGrant save(ExternalProjectAccessGrant entity);
    Optional<ExternalProjectAccessGrant> findByIdAndProjectId(UUID id, UUID projectId);
    List<ExternalProjectAccessGrant> findByProjectId(UUID projectId);
    Optional<ExternalProjectAccessGrant> findByProjectIdAndPortalAccountId(UUID projectId, UUID portalAccountId);
    List<ExternalProjectAccessGrant> findByPortalAccountId(UUID portalAccountId);
}
