package com.company.scopery.modules.servicesupport.supportcase.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClock;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicy;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicyRepository;
import com.company.scopery.modules.servicesupport.supportcase.application.command.TriageSupportCaseCommand;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class TriageSupportCaseAction {
    private final SupportCaseRepository cases; private final SlaPolicyRepository slaPolicies;
    private final SlaClockRepository slaClocks; private final SupportAuthorizationService auth;
    public TriageSupportCaseAction(SupportCaseRepository cases, SlaPolicyRepository slaPolicies,
                                   SlaClockRepository slaClocks, SupportAuthorizationService auth){
        this.cases=cases; this.slaPolicies=slaPolicies; this.slaClocks=slaClocks; this.auth=auth;
    }
    @Transactional
    public SupportCaseResponse execute(UUID workspaceId, UUID caseId, TriageSupportCaseCommand cmd) {
        auth.requireManage(workspaceId);
        SupportCase c = cases.findById(caseId).orElseThrow(() -> SupportExceptions.caseNotFound(caseId));
        try {
            SupportCase saved = cases.save(c.triage(cmd.ownerUserId()));
            startResolveClockIfPolicyPresent(workspaceId, saved, cmd.slaPolicyId());
            return SupportCaseResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw SupportExceptions.invalidStatus();
        }
    }
    private void startResolveClockIfPolicyPresent(UUID workspaceId, SupportCase supportCase, UUID slaPolicyId) {
        SlaPolicy policy = slaPolicyId == null ? null : slaPolicies.findById(slaPolicyId).orElse(null);
        if (policy == null) {
            var list = slaPolicies.findByWorkspaceId(workspaceId);
            policy = list.isEmpty() ? null : list.getFirst();
        }
        if (policy == null || policy.resolveMinutes() == null) return;
        slaClocks.save(SlaClock.start(workspaceId, supportCase.id(), policy.id(), "RESOLVE", policy.resolveMinutes()));
    }
}
