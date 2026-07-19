package com.company.scopery.modules.servicesupport.escalation.application.service;
import com.company.scopery.modules.servicesupport.escalation.application.response.SupportEscalationRuleResponse;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRuleRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service @Transactional(readOnly = true)
public class SupportEscalationRuleQueryService {
    private final SupportEscalationRuleRepository repo; private final SupportAuthorizationService auth;
    public SupportEscalationRuleQueryService(SupportEscalationRuleRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    public List<SupportEscalationRuleResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(SupportEscalationRuleResponse::from).toList();
    }
}
