package com.company.scopery.modules.servicesupport.escalation.application.action;
import com.company.scopery.modules.servicesupport.escalation.application.response.SupportEscalationRuleResponse;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRuleRepository;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class EnableSupportEscalationRuleAction {
    private final SupportEscalationRuleRepository repo; private final SupportAuthorizationService auth;
    public EnableSupportEscalationRuleAction(SupportEscalationRuleRepository repo, SupportAuthorizationService auth){ this.repo=repo; this.auth=auth; }
    @Transactional
    public SupportEscalationRuleResponse execute(UUID workspaceId, UUID ruleId) {
        auth.requireManage(workspaceId);
        var rule = repo.findById(ruleId).orElseThrow(() -> SupportExceptions.escalationRuleNotFound(ruleId));
        return SupportEscalationRuleResponse.from(repo.save(rule.enable()));
    }
}
