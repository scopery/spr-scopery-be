package com.company.scopery.modules.integrationhub.mapping.application.action;
import com.company.scopery.modules.integrationhub.mapping.application.response.DataMappingProfileResponse;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfileRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpdateDataMappingProfileAction {
    private final DataMappingProfileRepository profiles;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public UpdateDataMappingProfileAction(DataMappingProfileRepository profiles,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.profiles = profiles; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public DataMappingProfileResponse execute(UUID workspaceId, UUID profileId, String name, String mappingJson, String validationRulesJson) {
        auth.requireManage(workspaceId);
        var p = profiles.findById(profileId).orElseThrow(() -> IntegrationExceptions.mappingProfileNotFound(profileId));
        if (!workspaceId.equals(p.workspaceId())) throw IntegrationExceptions.mappingProfileNotFound(profileId);
        var saved = profiles.save(p.update(name, mappingJson, validationRulesJson));
        activity.logSuccess("INTEGRATION_MAPPING_PROFILE", saved.id(), "INTEGRATION_MAPPING_PROFILE_UPDATED", "Mapping profile updated");
        return DataMappingProfileResponse.from(saved);
    }
}
