package com.company.scopery.modules.trust.datasubject.application.action;
import com.company.scopery.modules.trust.datasubject.application.response.DataSubjectIndexResponse;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndex;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndexRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RebuildDataSubjectIndexAction {
    private final DataSubjectIndexRepository repo;
    private final TrustAuthorizationService auth;
    public RebuildDataSubjectIndexAction(DataSubjectIndexRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional
    public DataSubjectIndexResponse execute(UUID workspaceId) {
        auth.requireManage(workspaceId);
        var index = DataSubjectIndex.create(workspaceId, "WORKSPACE_SCAN", null, "Workspace Scan", null, null);
        var saved = repo.save(index.markRebuilt(0L));
        return DataSubjectIndexResponse.from(saved);
    }
}
