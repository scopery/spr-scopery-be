package com.company.scopery.modules.configuration.fieldvalue.application.service;
import com.company.scopery.modules.configuration.fieldvalue.application.response.CustomFieldValueResponse;
import com.company.scopery.modules.configuration.fieldvalue.domain.model.CustomFieldValueRepository;
import com.company.scopery.modules.configuration.shared.authorization.ConfigurationAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class FieldValueQueryService {
    private final CustomFieldValueRepository values; private final ConfigurationAuthorizationService authorization;
    public FieldValueQueryService(CustomFieldValueRepository values, ConfigurationAuthorizationService authorization) {
        this.values=values; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<CustomFieldValueResponse> list(UUID workspaceId, String objectType, UUID targetId) {
        authorization.requireFieldView(workspaceId);
        return values.findByWorkspaceObjectTarget(workspaceId, objectType, targetId).stream().map(CustomFieldValueResponse::from).toList();
    }
}
