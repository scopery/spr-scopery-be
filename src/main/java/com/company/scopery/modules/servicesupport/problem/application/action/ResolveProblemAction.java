package com.company.scopery.modules.servicesupport.problem.application.action;
import com.company.scopery.modules.servicesupport.problem.application.command.ResolveProblemCommand;
import com.company.scopery.modules.servicesupport.problem.application.response.SupportProblemResponse;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ResolveProblemAction {
    private final SupportProblemRecordRepository repo; private final SupportAuthorizationService auth;
    public ResolveProblemAction(SupportProblemRecordRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportProblemResponse execute(UUID workspaceId, UUID problemId, ResolveProblemCommand cmd) {
        auth.requireManage(workspaceId);
        var problem = repo.findById(problemId).orElseThrow(() -> SupportExceptions.problemNotFound(problemId));
        return SupportProblemResponse.from(repo.save(problem.resolve(cmd.rootCause(), cmd.workaround(), cmd.resolvedBy())));
    }
}
