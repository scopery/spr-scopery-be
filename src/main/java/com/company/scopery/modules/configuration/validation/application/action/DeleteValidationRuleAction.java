package com.company.scopery.modules.configuration.validation.application.action;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.validation.domain.model.CustomFieldValidationRuleRepository;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.validation.application.command.DeleteValidationRuleCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DeleteValidationRuleAction {
    private final CustomFieldValidationRuleRepository rules; private final ConfigurationAuthorizationService authorization;
    public DeleteValidationRuleAction(CustomFieldValidationRuleRepository rules, ConfigurationAuthorizationService authorization) {
        this.rules=rules; this.authorization=authorization;
    }
    @Transactional
    public void execute(DeleteValidationRuleCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        rules.findByIdAndWorkspaceId(c.ruleId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(c.ruleId()));
        rules.delete(c.ruleId());
    }
}
