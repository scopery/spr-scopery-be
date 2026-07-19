package com.company.scopery.modules.servicesupport.maintenance.application.action;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenanceWindowCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceWindowResponse;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindow;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlanRepository;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindowRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateMaintenanceWindowAction {
    private final MaintenanceWindowRepository repo; private final MaintenancePlanRepository plans; private final SupportAuthorizationService auth;
    public CreateMaintenanceWindowAction(MaintenanceWindowRepository repo, MaintenancePlanRepository plans, SupportAuthorizationService auth){ this.repo=repo; this.plans=plans; this.auth=auth; }
    @Transactional
    public MaintenanceWindowResponse execute(UUID workspaceId, CreateMaintenanceWindowCommand cmd) {
        auth.requireManage(workspaceId);
        plans.findById(cmd.maintenancePlanId()).orElseThrow(() -> SupportExceptions.maintenancePlanNotFound(cmd.maintenancePlanId()));
        var saved = repo.save(MaintenanceWindow.schedule(workspaceId, cmd.maintenancePlanId(), cmd.title(), cmd.scheduledStart(), cmd.scheduledEnd()));
        return MaintenanceWindowResponse.from(saved);
    }
}
