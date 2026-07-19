package com.company.scopery.modules.servicesupport.maintenance.application.action;
import com.company.scopery.modules.servicesupport.maintenance.application.command.CreateMaintenanceActivityCommand;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceActivityResponse;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivity;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivityRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateMaintenanceActivityAction {
    private final MaintenanceActivityRepository repo; private final SupportAuthorizationService auth;
    public CreateMaintenanceActivityAction(MaintenanceActivityRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public MaintenanceActivityResponse execute(UUID workspaceId, CreateMaintenanceActivityCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(MaintenanceActivity.create(workspaceId, cmd.maintenancePlanId(), cmd.activityType(), cmd.title()));
        return MaintenanceActivityResponse.from(saved);
    }
}
