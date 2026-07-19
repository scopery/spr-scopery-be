package com.company.scopery.modules.productivity.favorite.application.action;
import com.company.scopery.modules.productivity.favorite.domain.model.FavoriteItemRepository;
import com.company.scopery.modules.productivity.shared.activity.ProductivityActivityLogger;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.shared.constant.*;
import com.company.scopery.modules.productivity.shared.error.ProductivityExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.productivity.favorite.application.command.RemoveFavoriteCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RemoveFavoriteAction {
    private final FavoriteItemRepository favorites; private final ProductivityAuthorizationService authorization; private final ProductivityActivityLogger activityLogger;
    public RemoveFavoriteAction(FavoriteItemRepository favorites, ProductivityAuthorizationService authorization, ProductivityActivityLogger activityLogger) {
        this.favorites=favorites; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public void execute(RemoveFavoriteCommand c) {
        authorization.requireFavoriteManage(c.workspaceId());
        var f = favorites.findByIdAndWorkspaceId(c.favoriteId(), c.workspaceId()).orElseThrow(() -> ProductivityExceptions.favoriteNotFound(c.favoriteId()));
        favorites.save(f.archive());
        activityLogger.logSuccess(ProductivityEntityTypes.FAVORITE, c.favoriteId(), ProductivityActivityActions.FAVORITE_REMOVED, "Favorite removed");
    }
}
