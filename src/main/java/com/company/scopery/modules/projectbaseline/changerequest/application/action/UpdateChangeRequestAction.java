package com.company.scopery.modules.projectbaseline.changerequest.application.action;

import com.company.scopery.modules.projectbaseline.changerequest.application.command.UpdateChangeRequestCommand;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangePriority;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeType;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import com.company.scopery.modules.projectbaseline.shared.util.ProjectBaselineEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateChangeRequestAction {
    private final ChangeRequestRepository changeRequests;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public UpdateChangeRequestAction(ChangeRequestRepository changeRequests,
                                     ProjectBaselineAuthorizationService authorization,
                                     ProjectBaselinePlatformPublisher publisher) {
        this.changeRequests = changeRequests; this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public ChangeRequestResponse execute(UpdateChangeRequestCommand cmd) {
        authorization.requireChangeRequestUpdate(cmd.projectId());
        ChangeRequest cr = changeRequests.findByIdAndProjectId(cmd.changeRequestId(), cmd.projectId())
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(cmd.changeRequestId()));
        if (!cr.isEditable()) throw ProjectBaselineExceptions.changeRequestNotDraft(cr.id());
        ChangeType type = ProjectBaselineEnumParser.parseRequired(ChangeType.class, cmd.changeType(), "CHANGE_TYPE_INVALID", "changeType");
        ChangePriority priority = ProjectBaselineEnumParser.parseOptional(ChangePriority.class, cmd.priority(), "CHANGE_PRIORITY_INVALID", "priority");
        cr = changeRequests.save(cr.updateDraft(cmd.title(), cmd.description(), type, priority, cmd.reason(), cmd.baselineId()));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_UPDATED);
        return ChangeRequestResponse.from(cr);
    }
}
