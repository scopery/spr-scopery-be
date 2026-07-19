package com.company.scopery.modules.servicesupport.escalation.application.action;
import com.company.scopery.modules.servicesupport.escalation.application.command.CreateEscalationRuleCommand;
import com.company.scopery.modules.servicesupport.escalation.application.response.SupportEscalationRuleResponse;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRule;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRuleRepository;
import com.company.scopery.modules.servicesupport.shared.activity.SupportActivityLogger;
import com.company.scopery.modules.servicesupport.shared.authorization.SupportAuthorizationService;
import com.company.scopery.modules.servicesupport.shared.error.SupportExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSupportEscalationRuleAction {
    private final SupportEscalationRuleRepository repo; private final SupportAuthorizationService auth; private final SupportActivityLogger activity;
    public CreateSupportEscalationRuleAction(SupportEscalationRuleRepository repo, SupportAuthorizationService auth, SupportActivityLogger activity){ this.repo=repo; this.auth=auth; this.activity=activity; }
    @Transactional
    public SupportEscalationRuleResponse execute(UUID workspaceId, CreateEscalationRuleCommand cmd) {
        auth.requireManage(workspaceId);
        if (repo.existsByWorkspaceIdAndRuleCode(workspaceId, cmd.ruleCode())) throw SupportExceptions.escalationRuleCodeExists(cmd.ruleCode());
        var saved = repo.save(SupportEscalationRule.create(workspaceId, cmd.ruleCode(), cmd.name(), cmd.triggerType()));
        activity.logSuccess("ESCALATION_RULE", saved.id(), "ESCALATION_RULE_CREATED", "Escalation rule created: " + saved.ruleCode());
        return SupportEscalationRuleResponse.from(saved);
    }
}
