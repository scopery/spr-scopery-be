package com.company.scopery.modules.servicesupport.snapshot.application.service;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlanRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreachRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import com.company.scopery.modules.servicesupport.snapshot.application.response.SupportDashboardResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportDashboardService {
    private final SupportCaseRepository cases; private final SlaClockRepository clocks;
    private final SlaBreachRepository breaches; private final MaintenancePlanRepository plans;
    private final SupportAuthorizationService auth;
    public SupportDashboardService(SupportCaseRepository cases, SlaClockRepository clocks, SlaBreachRepository breaches,
                                   MaintenancePlanRepository plans, SupportAuthorizationService auth) {
        this.cases=cases; this.clocks=clocks; this.breaches=breaches; this.plans=plans; this.auth=auth;
    }
    public SupportDashboardResponse dashboard(UUID workspaceId) {
        auth.requireView(workspaceId);
        var all = cases.findByWorkspaceId(workspaceId);
        Instant now = Instant.now();
        long breachedClocks = clocks.findByWorkspaceId(workspaceId).stream()
                .filter(c -> SlaClockService.isBreached(c.dueAt(), now)).count();
        long openCases = all.stream().filter(c -> !"CLOSED".equals(c.status()) && !"CANCELLED".equals(c.status())).count();
        long openBreaches = breaches.findByWorkspaceId(workspaceId).stream().filter(b -> "OPEN".equals(b.status())).count();
        long maintenancePlans = plans.findByWorkspaceId(workspaceId).size();
        return new SupportDashboardResponse(openCases, breachedClocks, openBreaches, maintenancePlans,
                "SLA tracking is operational measurement, not legal enforcement");
    }
}
