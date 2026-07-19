package com.company.scopery.modules.servicesupport.costinput.application.action;
import com.company.scopery.modules.servicesupport.costinput.application.response.ServiceCostInputResponse;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInputRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveServiceCostInputAction {
    private final ServiceCostInputRepository repo; private final SupportAuthorizationService auth;
    public ApproveServiceCostInputAction(ServiceCostInputRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public ServiceCostInputResponse execute(UUID workspaceId, UUID inputId) {
        auth.requireManage(workspaceId);
        var input = repo.findById(inputId).orElseThrow(() -> SupportExceptions.costInputNotFound(inputId));
        return ServiceCostInputResponse.from(repo.save(input.approve()));
    }
}
