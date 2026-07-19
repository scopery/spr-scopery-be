package com.company.scopery.modules.servicesupport.costinput.application.action;
import com.company.scopery.modules.servicesupport.costinput.application.command.CreateServiceCostInputCommand;
import com.company.scopery.modules.servicesupport.costinput.application.response.ServiceCostInputResponse;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInput;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInputRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateServiceCostInputAction {
    private final ServiceCostInputRepository repo; private final SupportAuthorizationService auth;
    public CreateServiceCostInputAction(ServiceCostInputRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public ServiceCostInputResponse execute(UUID workspaceId, CreateServiceCostInputCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(ServiceCostInput.create(workspaceId, cmd.supportCaseId(), cmd.sourceType(), cmd.costAmount(), cmd.currency()));
        return ServiceCostInputResponse.from(saved);
    }
}
