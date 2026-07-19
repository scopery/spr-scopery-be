package com.company.scopery.modules.servicesupport.sla.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaBreachResponse;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaClockResponse;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaPolicyResponse;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaTargetResponse;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreachRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicyRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTargetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SlaQueryService {
    private final SlaPolicyRepository policies; private final SlaClockRepository clocks;
    private final SlaBreachRepository breaches; private final SlaTargetRepository targets;
    private final SupportAuthorizationService auth;
    public SlaQueryService(SlaPolicyRepository policies, SlaClockRepository clocks, SlaBreachRepository breaches,
                           SlaTargetRepository targets, SupportAuthorizationService auth){
        this.policies=policies; this.clocks=clocks; this.breaches=breaches; this.targets=targets; this.auth=auth;
    }
    public List<SlaPolicyResponse> listPolicies(UUID workspaceId) {
        auth.requireView(workspaceId);
        return policies.findByWorkspaceId(workspaceId).stream().map(SlaPolicyResponse::from).toList();
    }
    public List<SlaClockResponse> listClocks(UUID workspaceId) {
        auth.requireView(workspaceId);
        return clocks.findByWorkspaceId(workspaceId).stream().map(SlaClockResponse::from).toList();
    }
    public List<SlaBreachResponse> listBreaches(UUID workspaceId) {
        auth.requireView(workspaceId);
        return breaches.findByWorkspaceId(workspaceId).stream().map(SlaBreachResponse::from).toList();
    }
    public List<SlaTargetResponse> listTargets(UUID workspaceId) {
        auth.requireView(workspaceId);
        return targets.findByWorkspaceId(workspaceId).stream().map(SlaTargetResponse::from).toList();
    }
}
