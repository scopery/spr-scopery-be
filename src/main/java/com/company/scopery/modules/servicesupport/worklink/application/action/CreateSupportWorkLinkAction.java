package com.company.scopery.modules.servicesupport.worklink.application.action;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.worklink.application.command.CreateWorkLinkCommand;
import com.company.scopery.modules.servicesupport.worklink.application.response.SupportWorkLinkResponse;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLink;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLinkRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportWorkLinkAction {
    private final SupportWorkLinkRepository repo; private final SupportAuthorizationService auth;
    public CreateSupportWorkLinkAction(SupportWorkLinkRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportWorkLinkResponse execute(UUID workspaceId, CreateWorkLinkCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(SupportWorkLink.create(workspaceId, cmd.supportCaseId(), cmd.targetObjectType(), cmd.targetObjectId(), cmd.linkType()));
        return SupportWorkLinkResponse.from(saved);
    }
}
