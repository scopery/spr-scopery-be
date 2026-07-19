package com.company.scopery.modules.servicesupport.assignment.application.service;
import com.company.scopery.modules.servicesupport.assignment.application.response.SupportAssignmentResponse;
import com.company.scopery.modules.servicesupport.assignment.domain.model.SupportAssignmentRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportAssignmentQueryService {
    private final SupportAssignmentRepository repo; private final SupportAuthorizationService auth;
    public SupportAssignmentQueryService(SupportAssignmentRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportAssignmentResponse> listByCase(UUID workspaceId, UUID caseId) {
        auth.requireView(workspaceId);
        return repo.findBySupportCaseId(caseId).stream().map(SupportAssignmentResponse::from).toList();
    }
}
