package com.company.scopery.modules.servicesupport.effort.application.action;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import com.company.scopery.modules.servicesupport.effort.application.response.SupportEffortResponse;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecordRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CancelSupportEffortAction {
    private final SupportEffortRecordRepository efforts; private final SupportCaseRepository cases;
    private final SupportAuthorizationService auth; private final SupportActivityLogger activity; private final ApplicationEventPublisher events;
    public CancelSupportEffortAction(SupportEffortRecordRepository efforts, SupportCaseRepository cases,
            SupportAuthorizationService auth, SupportActivityLogger activity, ApplicationEventPublisher events){
        this.efforts=efforts; this.cases=cases; this.auth=auth; this.activity=activity; this.events=events;
    }
    @Transactional
    public SupportEffortResponse execute(UUID workspaceId, UUID effortId) {
        auth.requireManage(workspaceId);
        var effort = efforts.findById(effortId).orElseThrow(() -> SupportExceptions.effortNotFound(effortId));
        var saved = efforts.save(effort.cancel());
        activity.logSuccess("SUPPORT_EFFORT", saved.id(), "SUPPORT_EFFORT_CANCELLED", "Support effort cancelled");
        cases.findById(effort.supportCaseId()).ifPresent(c -> {
            if (c.projectId() != null) events.publishEvent(new ProfitabilityRebuildRequestedEvent(c.projectId(), "SUPPORT_EFFORT_CANCELLED"));
        });
        return SupportEffortResponse.from(saved);
    }
}
