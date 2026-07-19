package com.company.scopery.modules.externalparty.organization.domain.model;
import java.util.*;
public interface ExternalOrganizationRepository {
    ExternalOrganization save(ExternalOrganization entity);
    Optional<ExternalOrganization> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<ExternalOrganization> findByWorkspaceId(UUID workspaceId);
}
