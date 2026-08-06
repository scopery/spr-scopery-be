package com.company.scopery.modules.specpack.agentsession.application.action;

import com.company.scopery.modules.specpack.agentsession.application.command.AdvanceStageCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdvanceStageAction {

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackActivityLogger activityLogger;

    public AdvanceStageAction(SpecPackAgentSessionRepository sessionRepository,
                               SpecPackActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentSessionResponse execute(AdvanceStageCommand command) {
        SpecPackAgentSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(command.sessionId()));

        if (session.status() != AgentSessionStatus.ACTIVE) {
            throw SpecPackExceptions.agentSessionNotActive(command.sessionId());
        }

        SpecPackAgentStage stage = sessionRepository.findStageBySessionIdAndCode(command.sessionId(), command.stageCode())
                .orElseThrow(() -> SpecPackExceptions.agentStageNotFound(command.sessionId(), command.stageCode()));

        stage.complete(command.result());
        sessionRepository.saveStage(stage);

        AgentStageCode completedCode = AgentStageCode.valueOf(command.stageCode());
        AgentStageCode[] allCodes = AgentStageCode.values();
        int completedIndex = completedCode.ordinal();

        if (completedCode == AgentStageCode.COMPLETED || completedIndex >= allCodes.length - 1) {
            session.complete();
        } else {
            AgentStageCode nextCode = allCodes[completedIndex + 1];
            session.advanceToStage(nextCode);
        }

        SpecPackAgentSession savedSession = sessionRepository.save(session);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_AGENT_SESSION,
                savedSession.id(),
                SpecPackActivityActions.AGENT_STAGE_COMPLETED,
                "Stage " + command.stageCode() + " completed for session: " + savedSession.id()
        );

        return AgentSessionResponse.from(savedSession);
    }
}
