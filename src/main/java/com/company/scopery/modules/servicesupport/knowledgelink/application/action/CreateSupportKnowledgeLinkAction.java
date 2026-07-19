package com.company.scopery.modules.servicesupport.knowledgelink.application.action;
import com.company.scopery.modules.servicesupport.knowledgelink.application.command.CreateKnowledgeLinkCommand;
import com.company.scopery.modules.servicesupport.knowledgelink.application.response.SupportKnowledgeLinkResponse;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLink;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLinkRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportKnowledgeLinkAction {
    private final SupportKnowledgeLinkRepository repo; private final SupportAuthorizationService auth;
    public CreateSupportKnowledgeLinkAction(SupportKnowledgeLinkRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportKnowledgeLinkResponse execute(UUID workspaceId, CreateKnowledgeLinkCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(SupportKnowledgeLink.create(workspaceId, cmd.supportCaseId(), cmd.documentId(), cmd.linkType(), cmd.clientVisible()));
        return SupportKnowledgeLinkResponse.from(saved);
    }
}
