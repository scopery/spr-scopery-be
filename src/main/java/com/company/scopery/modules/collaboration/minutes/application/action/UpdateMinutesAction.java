package com.company.scopery.modules.collaboration.minutes.application.action;
import com.company.scopery.modules.collaboration.minutes.application.response.MeetingMinutesResponse;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutesRepository;
import com.company.scopery.modules.collaboration.shared.activity.CollaborationActivityLogger;
import com.company.scopery.modules.collaboration.shared.authorization.CollaborationAuthorizationService;
import com.company.scopery.modules.collaboration.shared.constant.*;
import com.company.scopery.modules.collaboration.shared.error.CollaborationExceptions;
import org.springframework.stereotype.Component;
import com.company.scopery.modules.collaboration.minutes.application.command.UpdateMinutesCommand;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateMinutesAction {
    private final MeetingMinutesRepository minutesRepo; private final CollaborationAuthorizationService authorization; private final CollaborationActivityLogger activityLogger;
    public UpdateMinutesAction(MeetingMinutesRepository minutesRepo, CollaborationAuthorizationService authorization, CollaborationActivityLogger activityLogger) {
        this.minutesRepo=minutesRepo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public MeetingMinutesResponse execute(UpdateMinutesCommand c) {
        authorization.requireMinutesUpdate(c.projectId());
        var m = minutesRepo.findByIdAndProjectId(c.minutesId(), c.projectId()).orElseThrow(() -> CollaborationExceptions.minutesNotFound(c.minutesId()));
        try { m = m.update(c.summary(), c.decisions(), c.actions(), c.clientSummary()); }
        catch (IllegalStateException ex) { throw CollaborationExceptions.minutesImmutable(c.minutesId()); }
        m = minutesRepo.save(m);
        activityLogger.logSuccess(CollaborationEntityTypes.MINUTES, m.id(), CollaborationActivityActions.MINUTES_UPDATED, "Minutes updated");
        return MeetingMinutesResponse.from(m);
    }
}
