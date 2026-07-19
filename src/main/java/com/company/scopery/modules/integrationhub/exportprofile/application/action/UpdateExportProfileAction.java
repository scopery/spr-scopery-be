package com.company.scopery.modules.integrationhub.exportprofile.application.action;
import com.company.scopery.modules.integrationhub.exportprofile.application.response.ExportProfileResponse;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfileRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class UpdateExportProfileAction {
    private final ExportProfileRepository profiles;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public UpdateExportProfileAction(ExportProfileRepository profiles,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.profiles = profiles; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ExportProfileResponse execute(UUID workspaceId, UUID profileId, String name, String columnsJson,
            String filtersJson, String maskingPolicy) {
        auth.requireManage(workspaceId);
        var p = profiles.findById(profileId).orElseThrow(() -> IntegrationExceptions.exportProfileNotFound(profileId));
        if (!workspaceId.equals(p.workspaceId())) throw IntegrationExceptions.exportProfileNotFound(profileId);
        var saved = profiles.save(p.update(name, columnsJson, filtersJson, maskingPolicy));
        activity.logSuccess("INTEGRATION_EXPORT_PROFILE", saved.id(), "INTEGRATION_EXPORT_PROFILE_UPDATED", "Export profile updated");
        return ExportProfileResponse.from(saved);
    }
}
