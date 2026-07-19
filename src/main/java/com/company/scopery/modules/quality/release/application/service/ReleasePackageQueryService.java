package com.company.scopery.modules.quality.release.application.service;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ReleasePackageQueryService {
    private final ReleasePackageRepository repo; private final QualityAuthorizationService authorization;
    public ReleasePackageQueryService(ReleasePackageRepository repo, QualityAuthorizationService authorization) { this.repo=repo; this.authorization=authorization; }
    @Transactional(readOnly=true) public List<ReleasePackageResponse> list(UUID projectId) { authorization.requireReleaseView(projectId); return repo.findByProjectId(projectId).stream().map(ReleasePackageResponse::from).toList(); }
    @Transactional(readOnly=true) public ReleasePackageResponse get(UUID projectId, UUID id) {
        authorization.requireReleaseView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(ReleasePackageResponse::from).orElseThrow(() -> QualityExceptions.releaseNotFound(id));
    }
}
