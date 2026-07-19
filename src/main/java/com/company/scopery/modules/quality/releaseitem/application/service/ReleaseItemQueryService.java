package com.company.scopery.modules.quality.releaseitem.application.service;
import com.company.scopery.modules.quality.releaseitem.application.response.ReleaseItemResponse;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItemRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ReleaseItemQueryService {
    private final ReleaseItemRepository repo;
    private final QualityAuthorizationService authorization;
    public ReleaseItemQueryService(ReleaseItemRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ReleaseItemResponse> listByRelease(UUID projectId, UUID releasePackageId) {
        authorization.requireReleaseView(projectId);
        return repo.findByProjectIdAndReleasePackageId(projectId, releasePackageId).stream().map(ReleaseItemResponse::from).toList();
    }
}
