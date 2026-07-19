package com.company.scopery.modules.scope.scopeitem.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.scope.scopeitem.application.command.ArchiveScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveScopeItemAction {
    private final ScopeItemRepository items;
    private final ScopeAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public ArchiveScopeItemAction(ScopeItemRepository items, ScopeAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser) {
        this.items = items;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional
    public ScopeItemResponse execute(ArchiveScopeItemCommand command) {
        authorization.requireScopeUpdate(command.projectId());
        var actor = currentUser.resolveCurrentUser();
        var item = items.findByIdAndProjectId(command.itemId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.itemNotFound(command.itemId()));
        return ScopeItemResponse.from(items.save(item.archive(actor.id())));
    }
}
