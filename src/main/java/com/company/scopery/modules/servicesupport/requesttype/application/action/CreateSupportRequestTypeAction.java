package com.company.scopery.modules.servicesupport.requesttype.application.action;
import com.company.scopery.modules.servicesupport.requesttype.application.command.CreateSupportRequestTypeCommand;
import com.company.scopery.modules.servicesupport.requesttype.application.response.SupportRequestTypeResponse;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestType;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestTypeRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportRequestTypeAction {
    private final SupportRequestTypeRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateSupportRequestTypeAction(SupportRequestTypeRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public SupportRequestTypeResponse execute(UUID workspaceId, CreateSupportRequestTypeCommand cmd) {
        auth.requireManage(workspaceId);
        if (repo.existsByWorkspaceIdAndTypeCode(workspaceId, cmd.typeCode())) throw SupportExceptions.requestTypeCodeExists(cmd.typeCode());
        var saved = repo.save(SupportRequestType.create(workspaceId, cmd.typeCode(), cmd.name(), cmd.defaultPriority(), cmd.portalVisible()));
        activity.logSuccess("REQUEST_TYPE", saved.id(), "REQUEST_TYPE_CREATED", "Request type created: " + saved.typeCode());
        return SupportRequestTypeResponse.from(saved);
    }
}
