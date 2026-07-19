package com.company.scopery.modules.scope.scopepackage.application.service;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ScopePackageQueryService {
    private final ScopePackageRepository packages; private final ScopeAuthorizationService authorization;
    public ScopePackageQueryService(ScopePackageRepository packages, ScopeAuthorizationService authorization) {
        this.packages=packages; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ScopePackageResponse> list(UUID projectId){ authorization.requireScopeView(projectId); return packages.findByProjectId(projectId).stream().map(ScopePackageResponse::from).toList(); }
    @Transactional(readOnly=true)
    public ScopePackageResponse get(UUID projectId, UUID id){
        authorization.requireScopeView(projectId);
        return packages.findByIdAndProjectId(id, projectId).map(ScopePackageResponse::from).orElseThrow(() -> ScopeExceptions.packageNotFound(id));
    }
}
