package com.company.scopery.modules.raid.raiditem.application.service;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.enums.RaidItemType;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import com.company.scopery.modules.raid.shared.util.RaidEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RaidItemQueryService {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization;
    public RaidItemQueryService(RaidItemRepository items, RaidAuthorizationService authorization) {
        this.items=items; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RaidItemResponse> list(UUID projectId, String type) {
        authorization.requireView(projectId);
        if (type == null || type.isBlank()) return items.findByProjectId(projectId).stream().map(RaidItemResponse::from).toList();
        RaidItemType t = RaidEnumParser.parseRequired(RaidItemType.class, type, "type");
        return items.findByProjectIdAndType(projectId, t).stream().map(RaidItemResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RaidItemResponse get(UUID projectId, UUID id) {
        authorization.requireView(projectId);
        return items.findByIdAndProjectId(id, projectId).map(RaidItemResponse::from).orElseThrow(() -> RaidExceptions.itemNotFound(id));
    }
}
