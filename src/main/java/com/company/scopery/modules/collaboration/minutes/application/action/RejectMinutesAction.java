package com.company.scopery.modules.collaboration.minutes.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.minutes.application.command.RejectMinutesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RejectMinutesAction {
    private final MeetingMinutesRepository minutesRepo; private final CollaborationAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final CollaborationActivityLogger activityLogger;
    public RejectMinutesAction(MeetingMinutesRepository minutesRepo, CollaborationAuthorizationService authorization,
                               CurrentUserAuthorizationService currentUser, CollaborationActivityLogger activityLogger) {
        this.minutesRepo=minutesRepo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingMinutesResponse execute(RejectMinutesCommand c) {
        authorization.requireMinutesApprove(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var m = minutesRepo.findByIdAndProjectId(c.minutesId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.minutesNotFound(c.minutesId()));
        try { m = m.reject(actor.id(), c.reason()); } catch (IllegalStateException ex) { throw CollaborationExceptions.invalidStatus(ex.getMessage()); }
        m = minutesRepo.save(m);
        activityLogger.logSuccess(CollaborationEntityTypes.MINUTES, m.id(), CollaborationActivityActions.MINUTES_REJECTED, "Minutes rejected");
        return MeetingMinutesResponse.from(m);
    }
}
