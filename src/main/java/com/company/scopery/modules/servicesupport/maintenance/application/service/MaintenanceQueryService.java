package com.company.scopery.modules.servicesupport.maintenance.application.service;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceActivityResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenancePlanResponse;
import com.company.scopery.modules.servicesupport.maintenance.application.response.MaintenanceWindowResponse;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivityRepository;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlanRepository;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindowRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class MaintenanceQueryService {
    private final MaintenancePlanRepository plans; private final MaintenanceWindowRepository windows;
    private final MaintenanceActivityRepository activities; private final SupportAuthorizationService auth;
    public MaintenanceQueryService(MaintenancePlanRepository plans, MaintenanceWindowRepository windows,
                                   MaintenanceActivityRepository activities, SupportAuthorizationService auth){
        this.plans=plans; this.windows=windows; this.activities=activities; this.auth=auth;
    }
    public List<MaintenancePlanResponse> listPlans(UUID workspaceId) {
        auth.requireView(workspaceId);
        return plans.findByWorkspaceId(workspaceId).stream().map(MaintenancePlanResponse::from).toList();
    }
    public List<MaintenanceWindowResponse> listWindows(UUID workspaceId) {
        auth.requireView(workspaceId);
        return windows.findByWorkspaceId(workspaceId).stream().map(MaintenanceWindowResponse::from).toList();
    }
    public List<MaintenanceActivityResponse> listActivities(UUID workspaceId) {
        auth.requireView(workspaceId);
        return activities.findByWorkspaceId(workspaceId).stream().map(MaintenanceActivityResponse::from).toList();
    }
}
