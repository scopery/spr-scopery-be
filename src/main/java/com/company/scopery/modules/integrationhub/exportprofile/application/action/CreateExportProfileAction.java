package com.company.scopery.modules.integrationhub.exportprofile.application.action;
import com.company.scopery.modules.integrationhub.exportprofile.application.response.ExportProfileResponse;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfile;
import com.company.scopery.modules.integrationhub.exportprofile.domain.model.ExportProfileRepository;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateExportProfileAction {
    private final ExportProfileRepository profiles;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateExportProfileAction(ExportProfileRepository profiles,
            IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.profiles = profiles; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public ExportProfileResponse execute(UUID workspaceId, String code, String name, String format,
            String destination, String scope, UUID connectionId) {
        auth.requireManage(workspaceId);
        if (profiles.existsByWorkspaceIdAndProfileCode(workspaceId, code)) {
            throw IntegrationExceptions.exportProfileCodeExists(code);
        }
        var saved = profiles.save(ExportProfile.create(workspaceId, connectionId, code, name, format, destination, scope));
        activity.logSuccess("INTEGRATION_EXPORT_PROFILE", saved.id(), "INTEGRATION_EXPORT_PROFILE_CREATED", "Export profile created: " + code);
        return ExportProfileResponse.from(saved);
    }
}
