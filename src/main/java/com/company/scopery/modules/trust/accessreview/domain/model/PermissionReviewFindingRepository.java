package com.company.scopery.modules.trust.accessreview.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface PermissionReviewFindingRepository {
    PermissionReviewFinding save(PermissionReviewFinding f);
    Optional<PermissionReviewFinding> findById(UUID id);
    List<PermissionReviewFinding> findByWorkspaceId(UUID workspaceId);
}
