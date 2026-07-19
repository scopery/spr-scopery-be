package com.company.scopery.modules.projectbaseline.changeimpact.application.action;

import com.company.scopery.modules.projectbaseline.changeimpact.application.response.ChangeImpactResponse;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpact;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CalculateChangeImpactAction {
    private final ChangeRequestRepository changeRequests;
    private final ChangeRequestItemRepository items;
    private final ChangeImpactRepository impacts;
    private final ObjectMapper objectMapper;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public CalculateChangeImpactAction(ChangeRequestRepository changeRequests, ChangeRequestItemRepository items,
                                       ChangeImpactRepository impacts, ObjectMapper objectMapper,
                                       ProjectBaselineAuthorizationService authorization,
                                       ProjectBaselinePlatformPublisher publisher) {
        this.changeRequests = changeRequests; this.items = items; this.impacts = impacts;
        this.objectMapper = objectMapper; this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public ChangeImpactResponse execute(UUID projectId, UUID changeRequestId) {
        authorization.requireImpactCalculate(projectId);
        var cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (!cr.isEditable()) throw ProjectBaselineExceptions.changeRequestNotDraft(cr.id());
        BigDecimal hours = BigDecimal.ZERO;
        for (ChangeRequestItem item : items.findByChangeRequestId(changeRequestId)) {
            hours = hours.add(deltaHours(item.beforeSnapshotJson())).negate().add(deltaHours(item.afterSnapshotJson()));
        }
        ChangeImpact impact = impacts.findByChangeRequestId(changeRequestId)
                .orElseGet(() -> ChangeImpact.create(changeRequestId, projectId, null));
        impact = impacts.save(impact.update(impact.currencyCode(), impact.scopeImpact(), impact.scheduleImpactDays(),
                hours, impact.laborCostImpact(), impact.directCostImpact(), impact.overheadImpact(),
                impact.revenueImpact(), impact.grossMarginImpact(), impact.pbtImpact(), impact.quoteAmountImpact(),
                impact.riskImpact(), impact.impactSummaryJson()));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_IMPACT_CALCULATED);
        return ChangeImpactResponse.from(impact, authorization.canViewFinanceImpact(projectId));
    }

    private BigDecimal deltaHours(String json) {
        if (json == null || json.isBlank()) return BigDecimal.ZERO;
        try {
            JsonNode n = objectMapper.readTree(json);
            if (n.hasNonNull("estimateHours")) return new BigDecimal(n.get("estimateHours").asText());
        } catch (Exception ignored) {}
        return BigDecimal.ZERO;
    }
}
