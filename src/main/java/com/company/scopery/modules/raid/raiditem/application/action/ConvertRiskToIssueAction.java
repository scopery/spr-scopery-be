package com.company.scopery.modules.raid.raiditem.application.action;
import com.company.scopery.modules.raid.raiditem.application.response.RaidItemResponse;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItemRepository;
import com.company.scopery.modules.raid.shared.activity.RaidActivityLogger;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.constant.RaidActivityActions;
import com.company.scopery.modules.raid.shared.constant.RaidEntityTypes;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ConvertRiskToIssueAction {
    private final RaidItemRepository items; private final RaidAuthorizationService authorization; private final RaidActivityLogger activityLogger;
    public ConvertRiskToIssueAction(RaidItemRepository items, RaidAuthorizationService authorization, RaidActivityLogger activityLogger) {
        this.items=items; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RaidItemResponse execute(UUID projectId, UUID itemId) {
        authorization.requireConvert(projectId);
        var item = items.findByIdAndProjectId(itemId, projectId).orElseThrow(() -> RaidExceptions.itemNotFound(itemId));
        try { item = items.save(item.convertRiskToIssue()); }
        catch (IllegalStateException ex) { throw RaidExceptions.invalidStatus(ex.getMessage()); }
        activityLogger.logSuccess(RaidEntityTypes.ITEM, item.id(), RaidActivityActions.ITEM_CONVERTED, "Risk converted to issue");
        return RaidItemResponse.from(item);
    }
}
