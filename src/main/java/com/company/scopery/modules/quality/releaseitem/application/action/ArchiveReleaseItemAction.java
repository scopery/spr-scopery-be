package com.company.scopery.modules.quality.releaseitem.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.releaseitem.application.response.ReleaseItemResponse;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItemRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveReleaseItemAction {
    private final ReleaseItemRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveReleaseItemAction(ReleaseItemRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public ReleaseItemResponse execute(UUID projectId, UUID id) {
        authorization.requireReleaseUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.releaseNotFound(id));
        return ReleaseItemResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
