package com.company.scopery.modules.servicesupport.statushistory.application.service;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.statushistory.application.response.SupportStatusHistoryResponse;
import com.company.scopery.modules.servicesupport.statushistory.domain.model.SupportStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportStatusHistoryQueryService {
    private final SupportStatusHistoryRepository repo; private final SupportAuthorizationService auth;
    public SupportStatusHistoryQueryService(SupportStatusHistoryRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportStatusHistoryResponse> listByCase(UUID workspaceId, UUID caseId) {
        auth.requireView(workspaceId);
        return repo.findBySupportCaseId(caseId).stream().map(SupportStatusHistoryResponse::from).toList();
    }
}
