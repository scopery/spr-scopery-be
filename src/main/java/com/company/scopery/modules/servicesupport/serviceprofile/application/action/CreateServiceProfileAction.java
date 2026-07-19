package com.company.scopery.modules.servicesupport.serviceprofile.application.action;
import com.company.scopery.modules.servicesupport.serviceprofile.application.command.CreateServiceProfileCommand;
import com.company.scopery.modules.servicesupport.serviceprofile.application.response.ServiceProfileResponse;
import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfile;
import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfileRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateServiceProfileAction {
    private final ServiceProfileRepository repo; private final SupportAuthorizationService auth;
    public CreateServiceProfileAction(ServiceProfileRepository repo, SupportAuthorizationService auth){
        this.repo=repo; this.auth=auth;
    }
    @Transactional
    public ServiceProfileResponse execute(UUID workspaceId, CreateServiceProfileCommand cmd) {
        auth.requireManage(workspaceId);
        String scope = cmd.scopeType() == null ? "PROJECT" : cmd.scopeType();
        ServiceProfile p = ServiceProfile.create(workspaceId, scope, cmd.projectId());
        if (cmd.portalIntakeEnabled()) {
            p = new ServiceProfile(p.id(), p.workspaceId(), p.scopeType(), p.projectId(), true, p.status(), p.createdAt());
        }
        return ServiceProfileResponse.from(repo.save(p));
    }
}
