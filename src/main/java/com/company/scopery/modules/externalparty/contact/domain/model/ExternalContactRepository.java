package com.company.scopery.modules.externalparty.contact.domain.model;
import java.util.*;
public interface ExternalContactRepository {
    ExternalContact save(ExternalContact entity);
    Optional<ExternalContact> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ExternalContact> findByWorkspaceId(UUID workspaceId);
}
