package com.company.scopery.modules.productivity.pin.application.service;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.pin.application.response.PinnedItemResponse;
import com.company.scopery.modules.productivity.pin.domain.model.PinnedItemRepository;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class PinQueryService {
    private final PinnedItemRepository pins; private final ProductivityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser;
    public PinQueryService(PinnedItemRepository pins, ProductivityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.pins=pins; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional(readOnly=true)
    public List<PinnedItemResponse> list(UUID workspaceId) {
        authorization.requirePinManage(workspaceId);
        return pins.findActiveByWorkspaceAndOwner(workspaceId, currentUser.resolveCurrentUser().id()).stream().map(PinnedItemResponse::from).toList();
    }
}
