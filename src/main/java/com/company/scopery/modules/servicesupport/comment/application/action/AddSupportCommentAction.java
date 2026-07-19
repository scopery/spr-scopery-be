package com.company.scopery.modules.servicesupport.comment.application.action;
import com.company.scopery.modules.servicesupport.comment.application.command.AddSupportCommentCommand;
import com.company.scopery.modules.servicesupport.comment.application.response.SupportCommentResponse;
import com.company.scopery.modules.servicesupport.comment.domain.model.SupportComment;
import com.company.scopery.modules.servicesupport.comment.domain.model.SupportCommentRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class AddSupportCommentAction {
    private final SupportCaseRepository cases; private final SupportCommentRepository comments;
    private final SupportAuthorizationService auth;
    public AddSupportCommentAction(SupportCaseRepository cases, SupportCommentRepository comments, SupportAuthorizationService auth){
        this.cases=cases; this.comments=comments; this.auth=auth;
    }
    @Transactional
    public SupportCommentResponse execute(UUID workspaceId, UUID caseId, AddSupportCommentCommand cmd) {
        auth.requireManage(workspaceId);
        cases.findById(caseId).orElseThrow(() -> SupportExceptions.caseNotFound(caseId));
        String body = cmd.body() == null ? "" : cmd.body();
        boolean clientVisible = "CLIENT_VISIBLE".equalsIgnoreCase(cmd.visibility());
        SupportComment created = clientVisible
                ? SupportComment.clientVisible(workspaceId, caseId, body, cmd.authorUserId())
                : SupportComment.internal(workspaceId, caseId, body, cmd.authorUserId());
        return SupportCommentResponse.from(comments.save(created));
    }
}
