package com.company.scopery.modules.collaboration.minutes.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.minutes.application.command.SubmitMinutesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class SubmitMinutesAction {
    private final MeetingMinutesRepository minutesRepo; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public SubmitMinutesAction(MeetingMinutesRepository minutesRepo, CollaborationAuthorizationService authorization,
                               CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.minutesRepo=minutesRepo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingMinutesResponse execute(SubmitMinutesCommand c) {
        authorization.requireMinutesUpdate(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var m = minutesRepo.findByIdAndProjectId(c.minutesId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.minutesNotFound(c.minutesId()));
        try { m = m.submit(actor.id()); } catch (IllegalStateException ex) { throw CollaborationExceptions.invalidStatus(ex.getMessage()); }
        m = minutesRepo.save(m);
        activityLogger.logSuccess(CollaborationEntityTypes.MINUTES, m.id(), CollaborationActivityActions.MINUTES_SUBMITTED, "Minutes submitted");
        return MeetingMinutesResponse.from(m);
    }
}
