package com.company.scopery.modules.servicesupport.problem.application.service;
import com.company.scopery.modules.servicesupport.problem.application.response.SupportProblemResponse;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportProblemQueryService {
    private final SupportProblemRecordRepository repo; private final SupportAuthorizationService auth;
    public SupportProblemQueryService(SupportProblemRecordRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportProblemResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportProblemResponse::from).toList();
    }
}
