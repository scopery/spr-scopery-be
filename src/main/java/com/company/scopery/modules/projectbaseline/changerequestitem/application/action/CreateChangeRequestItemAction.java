package com.company.scopery.modules.projectbaseline.changerequestitem.application.action;

import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.command.CreateChangeRequestItemCommand;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.response.ChangeRequestItemResponse;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemOperation;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemTargetType;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import com.company.scopery.modules.projectbaseline.shared.util.ProjectBaselineEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateChangeRequestItemAction {
    private final ChangeRequestRepository changeRequests;
    private final ChangeRequestItemRepository items;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public CreateChangeRequestItemAction(ChangeRequestRepository changeRequests, ChangeRequestItemRepository items,
                                         ProjectBaselineAuthorizationService authorization,
                                         ProjectBaselinePlatformPublisher publisher) {
        this.changeRequests = changeRequests; this.items = items;
        this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public ChangeRequestItemResponse execute(CreateChangeRequestItemCommand cmd) {
        authorization.requireItemCreate(cmd.projectId());
        var cr = changeRequests.findByIdAndProjectId(cmd.changeRequestId(), cmd.projectId())
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(cmd.changeRequestId()));
        if (!cr.isEditable()) throw ProjectBaselineExceptions.changeRequestNotDraft(cr.id());
        ChangeItemTargetType targetType = ProjectBaselineEnumParser.parseRequired(ChangeItemTargetType.class, cmd.targetType(), "TARGET_TYPE_INVALID", "targetType");
        ChangeItemOperation operation = ProjectBaselineEnumParser.parseRequired(ChangeItemOperation.class, cmd.operation(), "OPERATION_INVALID", "operation");
        ChangeRequestItem item = ChangeRequestItem.create(cr.id(), cmd.projectId(), targetType, cmd.targetId(),
                operation, cmd.summary(), cmd.beforeSnapshotJson(), cmd.afterSnapshotJson(), cmd.applyPayloadJson());
        item = items.save(item);
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_ITEM_CREATED);
        return ChangeRequestItemResponse.from(item);
    }
}
