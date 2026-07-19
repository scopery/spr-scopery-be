package com.company.scopery.modules.productivity.favorite.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.favorite.application.response.FavoriteItemResponse;
import com.company.scopery.modules.productivity.favorite.domain.model.*;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.favorite.application.command.CreateFavoriteCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateFavoriteAction {
    private final FavoriteItemRepository favorites; private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final ProductivityActivityLogger activityLogger;
    public CreateFavoriteAction(FavoriteItemRepository favorites, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, ProductivityActivityLogger activityLogger) {
        this.favorites=favorites; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public FavoriteItemResponse execute(CreateFavoriteCommand c) {
        authorization.requireFavoriteManage(c.workspaceId());
        var user = currentUser.resolveCurrentUser();
        var f = favorites.save(FavoriteItem.create(c.workspaceId(), user.id(), c.targetType(), c.targetId(), c.label()));
        activityLogger.logSuccess(ProductivityEntityTypes.FAVORITE, f.id(), ProductivityActivityActions.FAVORITE_CREATED, "Favorite created");
        return FavoriteItemResponse.from(f);
    }
}
