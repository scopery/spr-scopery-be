package com.company.scopery.modules.productivity.pin.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.pin.application.response.PinnedItemResponse;
import com.company.scopery.modules.productivity.pin.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.pin.application.command.CreatePinCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreatePinAction {
    private final PinnedItemRepository pins; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public CreatePinAction(PinnedItemRepository pins, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.pins=pins; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public PinnedItemResponse execute(CreatePinCommand c) {
        authorization.requirePinManage(c.workspaceId());
        var user = currentUser.resolveCurrentUser();
        var p = pins.save(PinnedItem.create(c.workspaceId(), c.projectId(), c.scope(), user.id(), c.targetType(), c.targetId(), c.sortOrder() == null ? 0 : c.sortOrder()));
        activityLogger.logSuccess(ProductivityEntityTypes.PIN, p.id(), ProductivityActivityActions.PIN_CREATED, "Pin created");
        return PinnedItemResponse.from(p);
    }
}
