package com.company.scopery.modules.configuration.validation.application.service;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import com.company.scopery.modules.configuration.validation.application.response.CustomFieldValidationRuleResponse;
import com.company.scopery.modules.configuration.validation.domain.model.CustomFieldValidationRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ValidationRuleQueryService {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldValidationRuleRepository rules;
    private final ConfigurationAuthorizationService authorization;
    public ValidationRuleQueryService(CustomFieldDefinitionRepository fields, CustomFieldValidationRuleRepository rules, ConfigurationAuthorizationService authorization) {
        this.fields=fields; this.rules=rules; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<CustomFieldValidationRuleResponse> list(UUID workspaceId, UUID fieldId) {
        authorization.requireFieldView(workspaceId);
        fields.findByIdAndWorkspaceId(fieldId, workspaceId).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(fieldId));
        return rules.findByFieldId(fieldId).stream().map(CustomFieldValidationRuleResponse::from).toList();
    }
}
