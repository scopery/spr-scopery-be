package com.company.scopery.modules.elicitation.session.application.action;

import com.company.scopery.modules.elicitation.session.application.command.StartSessionCommand;
import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSession;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartElicitationSessionAction {

    private final ElicitationSessionRepository sessionRepository;
    private final ElicitationActivityLogger activityLogger;

    public StartElicitationSessionAction(ElicitationSessionRepository sessionRepository,
                                          ElicitationActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationSessionResponse execute(StartSessionCommand command) {
        if (sessionRepository.existsActiveByProjectIdAndScopePackageId(
                command.projectId(), command.scopePackageId())) {
            throw ElicitationExceptions.sessionAlreadyActive(command.scopePackageId());
        }

        ElicitationSession session = ElicitationSession.create(
                command.projectId(), command.scopePackageId(), command.title());
        ElicitationSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                ElicitationEntityTypes.SESSION,
                saved.id(),
                ElicitationActivityActions.START_SESSION,
                "Elicitation session started for scope package: " + saved.scopePackageId()
        );

        return ElicitationSessionResponse.from(saved);
    }
}
