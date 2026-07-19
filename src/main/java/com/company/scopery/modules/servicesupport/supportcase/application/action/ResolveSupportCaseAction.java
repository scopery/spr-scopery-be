package com.company.scopery.modules.servicesupport.supportcase.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreach;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreachRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant; import java.util.UUID;
@Component
public class ResolveSupportCaseAction {
    private final SupportCaseRepository cases; private final SlaClockRepository slaClocks;
    private final SlaBreachRepository slaBreaches; private final SupportAuthorizationService auth;
    public ResolveSupportCaseAction(SupportCaseRepository cases, SlaClockRepository slaClocks,
                                    SlaBreachRepository slaBreaches, SupportAuthorizationService auth){
        this.cases=cases; this.slaClocks=slaClocks; this.slaBreaches=slaBreaches; this.auth=auth;
    }
    @Transactional
    public SupportCaseResponse execute(UUID workspaceId, UUID caseId) {
        auth.requireManage(workspaceId);
        SupportCase c = cases.findById(caseId).orElseThrow(() -> SupportExceptions.caseNotFound(caseId));
        try {
            SupportCase saved;
            try { saved = cases.save(c.startProgress().resolve()); }
            catch (IllegalStateException ex) { saved = cases.save(c.resolve()); }
            completeClocksAndRecordBreaches(workspaceId, saved.id());
            return SupportCaseResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw SupportExceptions.invalidStatus();
        }
    }
    private void completeClocksAndRecordBreaches(UUID workspaceId, UUID caseId) {
        Instant now = Instant.now();
        for (var clock : slaClocks.findBySupportCaseId(caseId)) {
            if ("COMPLETED".equals(clock.status())) continue;
            if (SlaClockService.isBreached(clock.dueAt(), now) && !"BREACHED".equals(clock.status())) {
                var breached = slaClocks.save(clock.markBreached());
                slaBreaches.save(SlaBreach.open(workspaceId, caseId, breached.id(), breached.clockType()));
            } else {
                slaClocks.save(clock.complete());
            }
        }
    }
}
