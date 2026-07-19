package com.company.scopery.modules.raid.decisionlink.application.service;

import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.decisionlink.application.response.DecisionLinkResponse;
import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLinkRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DecisionLinkQueryService {
    private final DecisionRecordRepository decisions;
    private final DecisionLinkRepository links;
    private final RaidAuthorizationService authorization;

    public DecisionLinkQueryService(DecisionRecordRepository decisions, DecisionLinkRepository links,
                                    RaidAuthorizationService authorization) {
        this.decisions = decisions;
        this.links = links;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<DecisionLinkResponse> list(UUID projectId, UUID decisionId) {
        authorization.requireDecisionView(projectId);
        decisions.findByIdAndProjectId(decisionId, projectId)
                .orElseThrow(() -> RaidExceptions.decisionNotFound(decisionId));
        return links.findByDecisionId(decisionId).stream().map(DecisionLinkResponse::from).toList();
    }
}
