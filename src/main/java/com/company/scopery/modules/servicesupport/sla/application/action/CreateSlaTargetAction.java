package com.company.scopery.modules.servicesupport.sla.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.sla.application.command.CreateSlaTargetCommand;
import com.company.scopery.modules.servicesupport.sla.application.response.SlaTargetResponse;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTarget;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTargetRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSlaTargetAction {
    private final SlaTargetRepository repo; private final SupportAuthorizationService auth;
    public CreateSlaTargetAction(SlaTargetRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SlaTargetResponse execute(UUID workspaceId, CreateSlaTargetCommand cmd) {
        auth.requireManage(workspaceId);
        return SlaTargetResponse.from(repo.save(SlaTarget.create(workspaceId, cmd.slaPolicyId(), cmd.targetType(), cmd.durationMinutes())));
    }
}
