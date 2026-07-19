package com.company.scopery.modules.configuration.customfield.application.service;
import com.company.scopery.modules.configuration.customfield.application.response.CustomFieldDefinitionResponse;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinitionRepository;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import com.company.scopery.modules.configuration.shared.error.ConfigurationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class CustomFieldQueryService {
    private final CustomFieldDefinitionRepository fields; private final ConfigurationAuthorizationService authorization;
    public CustomFieldQueryService(CustomFieldDefinitionRepository fields, ConfigurationAuthorizationService authorization) { this.fields=fields; this.authorization=authorization; }
    @Transactional(readOnly=true)
    public List<CustomFieldDefinitionResponse> list(UUID workspaceId) {
        authorization.requireFieldView(workspaceId);
        return fields.findByWorkspaceId(workspaceId).stream().map(CustomFieldDefinitionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public CustomFieldDefinitionResponse get(UUID workspaceId, UUID fieldId) {
        authorization.requireFieldView(workspaceId);
        return CustomFieldDefinitionResponse.from(fields.findByIdAndWorkspaceId(fieldId, workspaceId).orElseThrow(() -> ConfigurationExceptions.fieldNotFound(fieldId)));
    }
}
