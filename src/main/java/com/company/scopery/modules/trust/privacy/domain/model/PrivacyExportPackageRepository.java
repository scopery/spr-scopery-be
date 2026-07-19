package com.company.scopery.modules.trust.privacy.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface PrivacyExportPackageRepository {
    PrivacyExportPackage save(PrivacyExportPackage pkg);
    Optional<PrivacyExportPackage> findById(UUID id);
    List<PrivacyExportPackage> findByWorkspaceId(UUID workspaceId);
}
