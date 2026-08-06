package com.company.scopery.modules.specpack.agentsession.application.action;

import com.company.scopery.modules.specpack.agentsession.application.command.CancelSessionCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelSessionAction {

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackActivityLogger activityLogger;

    public CancelSessionAction(SpecPackAgentSessionRepository sessionRepository,
                                SpecPackActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentSessionResponse execute(CancelSessionCommand command) {
        SpecPackAgentSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(command.sessionId()));

        if (session.status() != AgentSessionStatus.ACTIVE) {
            throw SpecPackExceptions.agentSessionNotActive(command.sessionId());
        }

        session.cancel();
        SpecPackAgentSession saved = sessionRepository.save(session);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_AGENT_SESSION,
                saved.id(),
                SpecPackActivityActions.AGENT_SESSION_CANCELLED,
                "Agent session cancelled: " + saved.id()
        );

        return AgentSessionResponse.from(saved);
    }
}
