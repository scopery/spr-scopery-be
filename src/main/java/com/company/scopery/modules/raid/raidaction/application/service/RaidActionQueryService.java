package com.company.scopery.modules.raid.raidaction.application.service;
import com.company.scopery.modules.raid.raidaction.application.response.RaidActionResponse;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidActionRepository;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RaidActionQueryService {
    private final RaidActionRepository actions; private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public RaidActionQueryService(RaidActionRepository actions, RaidItemRepository items, RaidAuthorizationService authorization) {
        this.actions=actions; this.items=items; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RaidActionResponse> listByItem(UUID projectId, UUID raidItemId) {
        authorization.requireView(projectId);
        items.findByIdAndProjectId(raidItemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(raidItemId));
        return actions.findByRaidItemId(raidItemId).stream().map(RaidActionResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RaidActionResponse get(UUID projectId, UUID actionId) {
        authorization.requireView(projectId);
        return actions.findByIdAndProjectId(actionId, projectId).map(RaidActionResponse::from)
                .orElseThrow(() -> RaidExceptions.actionNotFound(actionId));
    }
}
