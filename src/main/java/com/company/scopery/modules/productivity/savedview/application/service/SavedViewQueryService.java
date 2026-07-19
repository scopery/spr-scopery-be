package com.company.scopery.modules.productivity.savedview.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.savedview.application.response.SavedViewResponse;
import com.company.scopery.modules.productivity.savedview.domain.model.SavedViewRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class SavedViewQueryService {
    private final SavedViewRepository views; private final ProductivityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public SavedViewQueryService(SavedViewRepository views, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.views=views; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<SavedViewResponse> list(UUID workspaceId) {
        authorization.requireSavedViewManage(workspaceId);
        return views.findActiveByWorkspaceAndOwner(workspaceId, currentUser.resolveCurrentUser().id()).stream().map(SavedViewResponse::from).toList();
    }
}
