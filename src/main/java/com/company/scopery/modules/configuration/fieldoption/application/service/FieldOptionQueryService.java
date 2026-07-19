package com.company.scopery.modules.configuration.fieldoption.application.service;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.fieldoption.application.response.CustomFieldOptionResponse;
import com.company.scopery.modules.configuration.fieldoption.domain.model.CustomFieldOptionRepository;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class FieldOptionQueryService {
    private final CustomFieldDefinitionRepository fields; private final CustomFieldOptionRepository options;
    private final ConfigurationAuthorizationService authorization;
    public FieldOptionQueryService(CustomFieldDefinitionRepository fields, CustomFieldOptionRepository options, ConfigurationAuthorizationService authorization) {
        this.fields=fields; this.options=options; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<CustomFieldOptionResponse> list(UUID workspaceId, UUID fieldId) {
        authorization.requireFieldView(workspaceId);
        fields.findByIdAndWorkspaceId(fieldId, workspaceId).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(fieldId));
        return options.findByFieldId(fieldId).stream().map(CustomFieldOptionResponse::from).toList();
    }
}
