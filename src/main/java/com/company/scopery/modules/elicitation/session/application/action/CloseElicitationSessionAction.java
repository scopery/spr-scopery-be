package com.company.scopery.modules.elicitation.session.application.action;

import com.company.scopery.modules.elicitation.session.application.command.CloseSessionCommand;
import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CloseElicitationSessionAction {

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationActivityLogger activityLogger;

    public CloseElicitationSessionAction(ElicitationSessionRepository sessionRepository,
                                          ElicitationActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSessionResponse execute(CloseSessionCommand command) {
        ElicitationSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(command.sessionId()));

        if (session.status() == SessionStatus.CLOSED) {
            throw ElicitationExceptions.sessionAlreadyClosed(session.id());
        }
        if (session.status() == SessionStatus.CANCELLED) {
            throw ElicitationExceptions.sessionAlreadyCancelled(session.id());
        }

        session.close();
        ElicitationSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                saved.id(),
                ElicitationActivityActions.CLOSE_SESSION,
                "Elicitation session closed"
        );

        return ElicitationSessionResponse.from(saved);
    }
}
