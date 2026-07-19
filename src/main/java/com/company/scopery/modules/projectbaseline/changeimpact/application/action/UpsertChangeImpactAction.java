package com.company.scopery.modules.projectbaseline.changeimpact.application.action;

import com.company.scopery.modules.projectbaseline.changeimpact.application.response.ChangeImpactResponse;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpact;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.changeimpact.http.request.UpdateChangeImpactRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpsertChangeImpactAction {
    private final ChangeRequestRepository changeRequests;
    private final ChangeImpactRepository impacts;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public UpsertChangeImpactAction(ChangeRequestRepository changeRequests, ChangeImpactRepository impacts,
                                    ProjectBaselineAuthorizationService authorization,
                                    ProjectBaselinePlatformPublisher publisher) {
        this.changeRequests = changeRequests; this.impacts = impacts;
        this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public ChangeImpactResponse execute(UUID projectId, UUID changeRequestId, UpdateChangeImpactRequest req) {
        authorization.requireImpactUpdate(projectId);
        var cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (!cr.isEditable()) throw ProjectBaselineExceptions.changeRequestNotDraft(cr.id());
        ChangeImpact impact = impacts.findByChangeRequestId(changeRequestId)
                .orElseGet(() -> ChangeImpact.create(changeRequestId, projectId, req.currencyCode()));
        impact = impacts.save(impact.update(req.currencyCode(), req.scopeImpact(), req.scheduleImpactDays(),
                req.estimateHoursImpact(), req.laborCostImpact(), req.directCostImpact(), req.overheadImpact(),
                req.revenueImpact(), req.grossMarginImpact(), req.pbtImpact(), req.quoteAmountImpact(),
                req.riskImpact(), req.impactSummaryJson()));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_IMPACT_UPDATED);
        return ChangeImpactResponse.from(impact, authorization.canViewFinanceImpact(projectId));
    }
}
