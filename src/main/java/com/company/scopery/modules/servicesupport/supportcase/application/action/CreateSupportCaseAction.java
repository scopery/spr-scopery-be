package com.company.scopery.modules.servicesupport.supportcase.application.action;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.supportcase.application.command.CreateSupportCaseCommand;
import com.company.scopery.modules.servicesupport.supportcase.application.response.SupportCaseResponse;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportCaseAction {
    private final SupportCaseRepository cases; private final SupportAuthorizationService auth;
    private final SupportActivityLogger activity;
    public CreateSupportCaseAction(SupportCaseRepository cases, SupportAuthorizationService auth, SupportActivityLogger activity){
        this.cases=cases; this.auth=auth; this.activity=activity;
    }
    @Transactional
    public SupportCaseResponse execute(UUID workspaceId, CreateSupportCaseCommand cmd) {
        auth.requireManage(workspaceId);
        String type = cmd.requestTypeCode() == null ? "QUESTION" : cmd.requestTypeCode();
        String priority = cmd.priority() == null ? "NORMAL" : cmd.priority();
        String source = cmd.source() == null ? "INTERNAL_CREATE" : cmd.source();
        String title = cmd.title() == null ? "Untitled" : cmd.title();
        SupportCase saved = cases.save(SupportCase.create(workspaceId, cmd.projectId(), type, priority, title, source, cmd.portalVisible()));
        activity.logSuccess("SUPPORT_CASE", saved.id(), "SUPPORT_CASE_CREATED", "Case created " + saved.caseNumber());
        return SupportCaseResponse.from(saved);
    }
}
