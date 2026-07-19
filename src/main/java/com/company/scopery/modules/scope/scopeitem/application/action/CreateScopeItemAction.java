package com.company.scopery.modules.scope.scopeitem.application.action;

import com.company.scopery.modules.scope.scopeitem.application.command.CreateScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateScopeItemAction {
    private final ScopePackageRepository packages;
    private final ScopeItemRepository items;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;

    public CreateScopeItemAction(ScopePackageRepository packages, ScopeItemRepository items,
                                 ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.packages = packages;
        this.items = items;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ScopeItemResponse execute(CreateScopeItemCommand command) {
        authorization.requireScopeCreate(command.projectId());
        var pkg = packages.findByIdAndProjectId(command.packageId(), command.projectId())
                .orElseThrow(() -> ScopeExceptions.packageNotFound(command.packageId()));
        if (!pkg.isEditable()) {
            throw ScopeExceptions.packageImmutable(command.packageId());
        }
        if (command.title() == null || command.title().isBlank()) {
            throw ScopeExceptions.itemTitleRequired();
        }
        ScopeItemType type = ScopeEnumParser.parseRequired(ScopeItemType.class, command.type(), "type");
        boolean inScope = command.inScope() == null || command.inScope();
        boolean outOfScope = command.outOfScope() != null && command.outOfScope();
        try {
            ScopeItem item = ScopeItem.create(
                    pkg.id(), command.projectId(), pkg.workspaceId(), type,
                    command.code(), command.title().trim(), command.description(),
                    inScope, outOfScope, command.priority(),
                    command.acceptanceRequired() == null || command.acceptanceRequired(),
                    command.sortOrder());
            item = items.save(item);
            activityLogger.logSuccess(ScopeEntityTypes.ITEM, item.id(), ScopeActivityActions.ITEM_CREATED, "Scope item created");
            return ScopeItemResponse.from(item);
        } catch (IllegalArgumentException ex) {
            throw ScopeExceptions.invalidFlags();
        }
    }
}
