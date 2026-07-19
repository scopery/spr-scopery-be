package com.company.scopery.modules.trust.privacy.application.action;
import com.company.scopery.modules.trust.privacy.application.response.PrivacyExportPackageResponse;
import com.company.scopery.modules.trust.privacy.domain.model.*;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreatePrivacyExportPackageAction {
    private final PrivacyExportPackageRepository packages;
    private final PrivacyRequestRepository privacy;
    private final TrustAuthorizationService auth;
    public CreatePrivacyExportPackageAction(PrivacyExportPackageRepository packages, PrivacyRequestRepository privacy, TrustAuthorizationService auth) {
        this.packages = packages; this.privacy = privacy; this.auth = auth;
    }
    @Transactional
    public PrivacyExportPackageResponse execute(UUID workspaceId, UUID requestId, String manifestJson) {
        auth.requireManage(workspaceId);
        var req = privacy.findById(requestId).orElseThrow(() -> TrustExceptions.privacyNotFound(requestId));
        if (!workspaceId.equals(req.workspaceId())) throw TrustExceptions.privacyNotFound(requestId);
        UUID subjectId = UUID.randomUUID();
        var saved = packages.save(PrivacyExportPackage.create(workspaceId, requestId, subjectId, manifestJson));
        return PrivacyExportPackageResponse.from(saved);
    }
}
