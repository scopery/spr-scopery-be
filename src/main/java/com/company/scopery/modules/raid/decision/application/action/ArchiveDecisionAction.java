package com.company.scopery.modules.raid.decision.application.action;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveDecisionAction {
    private final DecisionRecordRepository decisions; private final RaidAuthorizationService authorization;
    public ArchiveDecisionAction(DecisionRecordRepository decisions, RaidAuthorizationService authorization) {
        this.decisions=decisions; this.authorization=authorization;
    }
    @Transactional
    public DecisionRecordResponse execute(UUID projectId, UUID decisionId) {
        authorization.requireDecisionUpdate(projectId);
        var d = decisions.findByIdAndProjectId(decisionId, projectId).orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return DecisionRecordResponse.from(decisions.save(d.archive()));
    }
}
