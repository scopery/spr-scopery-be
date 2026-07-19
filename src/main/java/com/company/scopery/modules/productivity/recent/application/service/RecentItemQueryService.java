package com.company.scopery.modules.productivity.recent.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.recent.application.response.RecentItemResponse;
import com.company.scopery.modules.productivity.recent.domain.model.RecentItemRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RecentItemQueryService {
    private final RecentItemRepository recents; private final ProductivityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public RecentItemQueryService(RecentItemRepository recents, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.recents=recents; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<RecentItemResponse> list(UUID workspaceId) {
        authorization.requireNavView(workspaceId);
        return recents.findRecentByUser(currentUser.resolveCurrentUser().id(), 50).stream().map(RecentItemResponse::from).toList();
    }
}
