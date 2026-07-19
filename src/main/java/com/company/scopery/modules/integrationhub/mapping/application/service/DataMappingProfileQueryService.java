package com.company.scopery.modules.integrationhub.mapping.application.service;
import com.company.scopery.modules.integrationhub.mapping.application.response.DataMappingProfileResponse;
import com.company.scopery.modules.integrationhub.mapping.application.response.ExternalIdMappingResponse;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfileRepository;
import com.company.scopery.modules.integrationhub.mapping.domain.model.ExternalIdMappingRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DataMappingProfileQueryService {
    private final DataMappingProfileRepository profiles;
    private final ExternalIdMappingRepository externalIds;
    private final IntegrationAuthorizationService auth;
    public DataMappingProfileQueryService(DataMappingProfileRepository profiles, ExternalIdMappingRepository externalIds,
            IntegrationAuthorizationService auth) {
        this.profiles = profiles; this.externalIds = externalIds; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<DataMappingProfileResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return profiles.findByWorkspaceId(workspaceId).stream().map(DataMappingProfileResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public DataMappingProfileResponse getById(UUID workspaceId, UUID profileId) {
        auth.requireView(workspaceId);
        var p = profiles.findById(profileId).orElseThrow(() -> IntegrationExceptions.mappingProfileNotFound(profileId));
        if (!workspaceId.equals(p.workspaceId())) throw IntegrationExceptions.mappingProfileNotFound(profileId);
        return DataMappingProfileResponse.from(p);
    }
    @Transactional(readOnly = true)
    public List<ExternalIdMappingResponse> listExternalIdMappings(UUID workspaceId) {
        auth.requireView(workspaceId);
        return externalIds.findByWorkspaceId(workspaceId).stream().map(ExternalIdMappingResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public List<ExternalIdMappingResponse> listExternalIdMappingsByObject(UUID workspaceId, String objectType, UUID objectId) {
        auth.requireView(workspaceId);
        return externalIds.findByWorkspaceIdAndScoperyObjectTypeAndScoperyObjectId(workspaceId, objectType, objectId)
                .stream().map(ExternalIdMappingResponse::from).toList();
    }
}
