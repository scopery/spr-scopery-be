package com.company.scopery.modules.trust.datasubject.application.service;
import com.company.scopery.modules.trust.datasubject.application.response.DataSubjectIndexResponse;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndexRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DataSubjectIndexQueryService {
    private final DataSubjectIndexRepository repo;
    private final TrustAuthorizationService auth;
    public DataSubjectIndexQueryService(DataSubjectIndexRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<DataSubjectIndexResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(DataSubjectIndexResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public DataSubjectIndexResponse getById(UUID workspaceId, UUID subjectIndexId) {
        auth.requireView(workspaceId);
        return repo.findById(subjectIndexId).map(DataSubjectIndexResponse::from)
                .orElseThrow(TrustExceptions::retentionPolicyNotFound);
    }
}
