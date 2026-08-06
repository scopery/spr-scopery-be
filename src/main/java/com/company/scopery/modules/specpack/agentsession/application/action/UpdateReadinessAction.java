package com.company.scopery.modules.specpack.agentsession.application.action;

import com.company.scopery.modules.specpack.agentsession.application.command.UpdateReadinessCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.domain.enums.ReadinessStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationPriority;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationStatus;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarificationRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateReadinessAction {

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackClarificationRepository clarificationRepository;
    private final SpecPackActivityLogger activityLogger;

    public UpdateReadinessAction(SpecPackAgentSessionRepository sessionRepository,
                                  SpecPackClarificationRepository clarificationRepository,
                                  SpecPackActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.clarificationRepository = clarificationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentSessionResponse execute(UpdateReadinessCommand command) {
        SpecPackAgentSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(command.sessionId()));

        long blockingOpen = clarificationRepository.countBySessionIdAndPriorityAndStatus(
                command.sessionId(),
                ClarificationPriority.BLOCKING.name(),
                ClarificationStatus.OPEN.name()
        );

        ReadinessStatus readiness;
        if (blockingOpen > 0) {
            readiness = ReadinessStatus.NOT_READY;
        } else {
            long importantOpen = clarificationRepository.countBySessionIdAndPriorityAndStatus(
                    command.sessionId(),
                    ClarificationPriority.IMPORTANT.name(),
                    ClarificationStatus.OPEN.name()
            );
            readiness = importantOpen > 0 ? ReadinessStatus.READY_WITH_WARNINGS : ReadinessStatus.READY;
        }

        session.updateReadiness(readiness);
        SpecPackAgentSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_AGENT_SESSION,
                saved.id(),
                SpecPackActivityActions.READINESS_UPDATED,
                "Readiness updated to " + readiness.name() + " for session: " + saved.id()
        );

        return AgentSessionResponse.from(saved);
    }
}
