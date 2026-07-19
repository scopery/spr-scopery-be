package com.company.scopery.modules.productivity.favorite.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.favorite.application.response.FavoriteItemResponse;
import com.company.scopery.modules.productivity.favorite.domain.model.FavoriteItemRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class FavoriteQueryService {
    private final FavoriteItemRepository favorites; private final ProductivityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public FavoriteQueryService(FavoriteItemRepository favorites, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.favorites=favorites; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<FavoriteItemResponse> list(UUID workspaceId) {
        authorization.requireFavoriteManage(workspaceId);
        return favorites.findActiveByWorkspaceAndUser(workspaceId, currentUser.resolveCurrentUser().id()).stream().map(FavoriteItemResponse::from).toList();
    }
}
