package com.company.scopery.modules.configuration.validation.application.action;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.shared.activity.ConfigurationActivityLogger;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.constant.*;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.validation.application.response.CustomFieldValidationRuleResponse;
import com.company.scopery.modules.configuration.validation.domain.model.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.configuration.validation.application.command.CreateValidationRuleCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateValidationRuleAction {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldValidationRuleRepository rules;
    private final ConfigurationAuthorizationService authorization; private final ConfigurationActivityLogger activityLogger;
    public CreateValidationRuleAction(CustomFieldDefinitionRepository fields, CustomFieldValidationRuleRepository rules,
                                      ConfigurationAuthorizationService authorization, ConfigurationActivityLogger activityLogger) {
        this.fields=fields; this.rules=rules; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public CustomFieldValidationRuleResponse execute(CreateValidationRuleCommand c) {
        authorization.requireFieldUpdate(c.workspaceId());
        fields.findByIdAndWorkspaceId(c.fieldId(), c.workspaceId()).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(c.fieldId()));
        var r = rules.save(CustomFieldValidationRule.create(c.workspaceId(), c.fieldId(), c.ruleType(), c.config()));
        activityLogger.logSuccess(ConfigurationEntityTypes.FIELD_VALIDATION, r.id(), ConfigurationActivityActions.VALIDATION_CREATED, "Validation rule created");
        return CustomFieldValidationRuleResponse.from(r);
    }
}
