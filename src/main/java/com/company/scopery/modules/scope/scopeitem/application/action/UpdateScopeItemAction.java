package com.company.scopery.modules.scope.scopeitem.application.action;

import com.company.scopery.modules.scope.scopeitem.application.command.UpdateScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateScopeItemAction {
    private final ScopeItemRepository items;
    private final ScopePackageRepository packages;
    private final ScopeAuthorizationService authorization;

    public UpdateScopeItemAction(ScopeItemRepository items, ScopePackageRepository packages,
                                 ScopeAuthorizationService authorization) {
        this.items = items;
        this.packages = packages;
        this.authorization = authorization;
    }

    @Transactional
    public ScopeItemResponse execute(UpdateScopeItemCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        var item = items.findByIdAndProjectId(command.itemId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.itemNotFound(command.itemId()));
        UUID packageId = item.scopePackageId();
        var pkg = packages.findByIdAndProjectId(packageId, command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(packageId));
        if (!pkg.isEditable()) {
            throw ScopeExceptions.packageImmutable(pkg.id());
        }
        boolean inScope = command.inScope() != null ? command.inScope() : item.inScope();
        boolean outOfScope = command.outOfScope() != null ? command.outOfScope() : item.outOfScope();
        try {
            item = items.save(item.update(
                    command.title() != null ? command.title() : item.title(),
                    command.description() != null ? command.description() : item.description(),
                    inScope,
                    outOfScope,
                    command.priority() != null ? command.priority() : item.priority(),
                    command.acceptanceRequired() != null ? command.acceptanceRequired() : item.acceptanceRequired(),
                    command.sortOrder() != null ? command.sortOrder() : item.sortOrder()));
            return ScopeItemResponse.from(item);
        } catch (IllegalArgumentException ex) {
            throw ScopeExceptions.invalidFlags();
        }
    }
}
