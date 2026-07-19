package com.company.scopery.modules.servicesupport.sla.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.sla.application.command.CreateSlaPolicyCommand;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaPolicyResponse;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicy;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaPolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSlaPolicyAction {
    private final SlaPolicyRepository repo; private final SupportAuthorizationService auth;
    public CreateSlaPolicyAction(SlaPolicyRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SlaPolicyResponse execute(UUID workspaceId, CreateSlaPolicyCommand cmd) {
        auth.requireManage(workspaceId);
        int first = cmd.firstResponseMinutes() == null ? 30 : cmd.firstResponseMinutes();
        int resolve = cmd.resolveMinutes() == null ? 240 : cmd.resolveMinutes();
        String code = cmd.policyCode() == null ? "SLA-" + UUID.randomUUID().toString().substring(0,6).toUpperCase() : cmd.policyCode();
        String name = cmd.name() == null ? "Default SLA" : cmd.name();
        return SlaPolicyResponse.from(repo.save(SlaPolicy.create(workspaceId, code, name, first, resolve)));
    }
}
