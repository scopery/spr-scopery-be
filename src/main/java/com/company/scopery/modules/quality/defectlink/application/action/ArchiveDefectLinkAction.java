package com.company.scopery.modules.quality.defectlink.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.defectlink.application.response.DefectLinkResponse;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLinkRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveDefectLinkAction {
    private final DefectLinkRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveDefectLinkAction(DefectLinkRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public DefectLinkResponse execute(UUID projectId, UUID id) {
        authorization.requireDefectUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.defectNotFound(id));
        return DefectLinkResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
