package com.company.scopery.modules.servicesupport.assignment.application.action;
import com.company.scopery.modules.servicesupport.assignment.application.command.AssignSupportCaseCommand;
import com.company.scopery.modules.servicesupport.assignment.application.response.SupportAssignmentResponse;
import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignment;
import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignmentRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class AssignSupportCaseAction {
    private final SupportCaseRepository cases; private final SupportAssignmentRepository assignments;
    private final SupportAuthorizationService auth;
    public AssignSupportCaseAction(SupportCaseRepository cases, SupportAssignmentRepository assignments, SupportAuthorizationService auth){
        this.cases=cases; this.assignments=assignments; this.auth=auth;
    }
    @Transactional
    public SupportAssignmentResponse execute(UUID workspaceId, UUID caseId, AssignSupportCaseCommand cmd) {
        auth.requireManage(workspaceId);
        cases.findById(caseId).orElseThrow(() -> SupportExceptions.caseNotFound(caseId));
        SupportAssignment saved;
        if (cmd.resourceProfileId() != null) {
            saved = assignments.save(SupportAssignment.assignResource(workspaceId, caseId, cmd.resourceProfileId()));
        } else {
            saved = assignments.save(SupportAssignment.assignUser(workspaceId, caseId, cmd.assigneeUserId()));
        }
        return SupportAssignmentResponse.from(saved);
    }
}
