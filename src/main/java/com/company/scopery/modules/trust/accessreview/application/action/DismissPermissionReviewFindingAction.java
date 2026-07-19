package com.company.scopery.modules.trust.accessreview.application.action;
import com.company.scopery.modules.trust.accessreview.application.response.PermissionReviewFindingResponse;
import com.company.scopery.modules.trust.accessreview.domain.model.PermissionReviewFindingRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DismissPermissionReviewFindingAction {
    private final PermissionReviewFindingRepository repo;
    private final TrustAuthorizationService auth;
    public DismissPermissionReviewFindingAction(PermissionReviewFindingRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public PermissionReviewFindingResponse execute(UUID workspaceId, UUID findingId) {
        auth.requireManage(workspaceId);
        var f = repo.findById(findingId).orElseThrow(TrustExceptions::accessReviewNotFound);
        if (!workspaceId.equals(f.workspaceId())) throw TrustExceptions.accessReviewNotFound();
        return PermissionReviewFindingResponse.from(repo.save(f.dismiss()));
    }
}
