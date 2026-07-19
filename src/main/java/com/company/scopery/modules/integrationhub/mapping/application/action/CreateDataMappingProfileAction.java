package com.company.scopery.modules.integrationhub.mapping.application.action;
import com.company.scopery.modules.integrationhub.mapping.application.response.DataMappingProfileResponse;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfile;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfileRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateDataMappingProfileAction {
    private final DataMappingProfileRepository profiles;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateDataMappingProfileAction(DataMappingProfileRepository profiles,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.profiles = profiles; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public DataMappingProfileResponse execute(UUID workspaceId, String code, String name, String targetObjectType,
            String sourceFormat, String mappingJson, UUID connectionId) {
        auth.requireManage(workspaceId);
        if (profiles.existsByWorkspaceIdAndMappingCode(workspaceId, code)) {
            throw IntegrationExceptions.mappingProfileCodeExists(code);
        }
        var saved = profiles.save(DataMappingProfile.create(workspaceId, connectionId, code, name, targetObjectType, sourceFormat, mappingJson));
        activity.logSuccess("INTEGRATION_MAPPING_PROFILE", saved.id(), "INTEGRATION_MAPPING_PROFILE_CREATED", "Mapping profile created: " + code);
        return DataMappingProfileResponse.from(saved);
    }
}
