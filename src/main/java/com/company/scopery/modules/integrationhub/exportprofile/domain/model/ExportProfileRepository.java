package com.company.scopery.modules.integrationhub.exportprofile.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ExportProfileRepository {
    ExportProfile save(ExportProfile p);
    Optional<ExportProfile> findById(UUID id);
    Optional<ExportProfile> findByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode);
    List<ExportProfile> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndProfileCode(UUID workspaceId, String profileCode);
}
