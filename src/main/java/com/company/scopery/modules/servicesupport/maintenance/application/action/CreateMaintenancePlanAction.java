package com.company.scopery.modules.servicesupport.maintenance.application.action;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenancePlanCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenancePlanResponse;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlan;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlanRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateMaintenancePlanAction {
    private final MaintenancePlanRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateMaintenancePlanAction(MaintenancePlanRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public MaintenancePlanResponse execute(UUID workspaceId, CreateMaintenancePlanCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(MaintenancePlan.create(workspaceId, cmd.projectId(), cmd.name(), cmd.plannedStart(), cmd.plannedEnd()));
        activity.logSuccess("MAINTENANCE_PLAN", saved.id(), "MAINTENANCE_PLAN_CREATED", "Maintenance plan created: " + saved.name());
        return MaintenancePlanResponse.from(saved);
    }
}
