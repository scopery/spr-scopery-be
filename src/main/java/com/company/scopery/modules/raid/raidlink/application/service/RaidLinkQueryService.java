package com.company.scopery.modules.raid.raidlink.application.service;

import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.raidlink.application.response.RaidLinkResponse;
import com.company.scopery.modules.raid.raidlink.domain.model.RaidLinkRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RaidLinkQueryService {
    private final RaidItemRepository items;
    private final RaidLinkRepository links;
    private final RaidAuthorizationService authorization;

    public RaidLinkQueryService(RaidItemRepository items, RaidLinkRepository links,
                                RaidAuthorizationService authorization) {
        this.items = items;
        this.links = links;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RaidLinkResponse> list(UUID projectId, UUID raidItemId) {
        authorization.requireView(projectId);
        items.findByIdAndProjectId(raidItemId, projectId)
                .orElseThrow(() -> RaidExceptions.itemNotFound(raidItemId));
        return links.findByRaidItemId(raidItemId).stream().map(RaidLinkResponse::from).toList();
    }
}
