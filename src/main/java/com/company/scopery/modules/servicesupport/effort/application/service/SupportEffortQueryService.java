package com.company.scopery.modules.servicesupport.effort.application.service;
import com.company.scopery.modules.servicesupport.effort.application.response.SupportEffortResponse;
import com.company.scopery.modules.servicesupport.effort.domain.model.SupportEffortRecordRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportEffortQueryService {
    private final SupportEffortRecordRepository repo; private final SupportAuthorizationService auth;
    public SupportEffortQueryService(SupportEffortRecordRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportEffortResponse> listByCase(UUID workspaceId, UUID caseId) {
        auth.requireView(workspaceId);
        return repo.findBySupportCaseId(caseId).stream().map(SupportEffortResponse::from).toList();
    }
    public List<SupportEffortResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportEffortResponse::from).toList();
    }
}
