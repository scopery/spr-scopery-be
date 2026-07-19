package com.company.scopery.modules.quality.defect.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveDefectAction(DefectRepository repo, QualityAuthorizationService authorization,
                               CurrentUserAuthorizationService currentUser) {
        this.repo = repo; this.authorization = authorization; this.currentUser = currentUser;
    }
    @Transactional
    public DefectResponse execute(UUID projectId, UUID defectId) {
        authorization.requireDefectUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var d = repo.findByIdAndProjectId(defectId, projectId).orElseThrow(() -> QualityExceptions.defectNotFound(defectId));
        return DefectResponse.from(repo.save(d.archive(actor.id())));
    }
}
