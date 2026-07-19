package com.company.scopery.modules.servicesupport.problem.application.action;
import com.company.scopery.modules.servicesupport.problem.application.command.CreateProblemCommand;
import com.company.scopery.modules.servicesupport.problem.application.response.SupportProblemResponse;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecord;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecordRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateProblemAction {
    private final SupportProblemRecordRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateProblemAction(SupportProblemRecordRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public SupportProblemResponse execute(UUID workspaceId, CreateProblemCommand cmd) {
        auth.requireManage(workspaceId);
        var saved = repo.save(SupportProblemRecord.create(workspaceId, cmd.projectId(), cmd.title()));
        activity.logSuccess("PROBLEM", saved.id(), "PROBLEM_CREATED", "Problem created: " + saved.problemNumber());
        return SupportProblemResponse.from(saved);
    }
}
