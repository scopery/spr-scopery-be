package com.company.scopery.modules.projectbaseline.changerequestitem.application.action;

import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteChangeRequestItemAction {
    private final ChangeRequestRepository changeRequests;
    private final ChangeRequestItemRepository items;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public DeleteChangeRequestItemAction(ChangeRequestRepository changeRequests, ChangeRequestItemRepository items,
                                         ProjectBaselineAuthorizationService authorization,
                                         ProjectBaselinePlatformPublisher publisher) {
        this.changeRequests = changeRequests; this.items = items;
        this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public void execute(UUID projectId, UUID changeRequestId, UUID itemId) {
        authorization.requireItemDelete(projectId);
        var cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (!cr.isEditable()) throw ProjectBaselineExceptions.changeRequestNotDraft(cr.id());
        var item = items.findByIdAndChangeRequestId(itemId, changeRequestId)
                .orElseThrow(() -> ProjectBaselineExceptions.itemNotFound(itemId));
        items.delete(item);
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_ITEM_DELETED);
    }
}
