package com.company.scopery.modules.clientportal.account.domain.model;
import java.util.*;
public interface ExternalPortalAccountRepository {
    ExternalPortalAccount save(ExternalPortalAccount entity);
    Optional<ExternalPortalAccount> findById(UUID id);
    Optional<ExternalPortalAccount> findByWorkspaceIdAndEmail(UUID workspaceId, String email);
}
