package com.company.scopery.modules.scope.scopeitem.application.service;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class ScopeItemQueryService {
    private final ScopeItemRepository items; private final ScopePackageRepository packages; private final ScopeAuthorizationService authorization;
    public ScopeItemQueryService(ScopeItemRepository items, ScopePackageRepository packages, ScopeAuthorizationService authorization) {
        this.items=items; this.packages=packages; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<ScopeItemResponse> listByPackage(UUID projectId, UUID packageId) {
        authorization.requireScopeView(projectId);
        packages.findByIdAndProjectId(packageId, projectId).orElseThrow(() -> ScopeExceptions.packageNotFound(packageId));
        return items.findByScopePackageId(packageId).stream().map(ScopeItemResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public ScopeItemResponse get(UUID projectId, UUID itemId) {
        authorization.requireScopeView(projectId);
        return items.findByIdAndProjectId(itemId, projectId).map(ScopeItemResponse::from).orElseThrow(() -> ScopeExceptions.itemNotFound(itemId));
    }
}
