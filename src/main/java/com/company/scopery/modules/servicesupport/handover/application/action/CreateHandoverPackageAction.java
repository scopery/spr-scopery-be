package com.company.scopery.modules.servicesupport.handover.application.action;
import com.company.scopery.modules.servicesupport.handover.application.command.CreateHandoverPackageCommand;
import com.company.scopery.modules.servicesupport.handover.application.response.ServiceHandoverPackageResponse;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackage;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackageRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateHandoverPackageAction {
    private final ServiceHandoverPackageRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateHandoverPackageAction(ServiceHandoverPackageRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public ServiceHandoverPackageResponse execute(UUID workspaceId, CreateHandoverPackageCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(ServiceHandoverPackage.create(workspaceId, cmd.projectId(), cmd.title()));
        activity.logSuccess("HANDOVER_PACKAGE", saved.id(), "HANDOVER_PACKAGE_CREATED", "Handover package created: " + saved.packageCode());
        return ServiceHandoverPackageResponse.from(saved);
    }
}
