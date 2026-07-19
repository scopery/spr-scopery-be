package com.company.scopery.modules.raid.decision.application.service;

import com.company.scopery.modules.raid.decision.application.response.DecisionImpactResponse;
import com.company.scopery.modules.raid.decision.application.response.DecisionOptionResponse;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpactRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionOptionRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DecisionQueryService {
    private final DecisionRecordRepository decisions;
    private final DecisionOptionRepository options;
    private final DecisionImpactRepository impacts;
    private final RaidAuthorizationService authorization;

    public DecisionQueryService(DecisionRecordRepository decisions, DecisionOptionRepository options,
                                DecisionImpactRepository impacts, RaidAuthorizationService authorization) {
        this.decisions = decisions;
        this.options = options;
        this.impacts = impacts;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<DecisionRecordResponse> list(UUID projectId) {
        authorization.requireDecisionView(projectId);
        return decisions.findByProjectId(projectId).stream().map(DecisionRecordResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DecisionRecordResponse get(UUID projectId, UUID id) {
        authorization.requireDecisionView(projectId);
        return decisions.findByIdAndProjectId(id, projectId).map(DecisionRecordResponse::from)
                .orElseThrow(() -> RaidExceptions.decisionNotFound(id));
    }

    @Transactional(readOnly = true)
    public List<DecisionOptionResponse> listOptions(UUID projectId, UUID decisionId) {
        authorization.requireDecisionView(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId)
                .orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return options.findByDecisionId(decisionId).stream().map(DecisionOptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Optional<DecisionImpactResponse> getImpact(UUID projectId, UUID decisionId) {
        authorization.requireDecisionView(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId)
                .orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        boolean maskFinance = !authorization.canViewFinance(projectId);
        return impacts.findByDecisionId(decisionId).map(i ->
                maskFinance ? DecisionImpactResponse.fromMasked(i) : DecisionImpactResponse.from(i));
    }
}
