package com.company.scopery.modules.specpack.agentsession.application.action;

import com.company.scopery.modules.specpack.agentsession.application.command.StartAgentSessionCommand;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartAgentSessionAction {

    private final SpecPackRepository specPackRepository;
    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackActivityLogger activityLogger;

    public StartAgentSessionAction(SpecPackRepository specPackRepository,
                                    SpecPackAgentSessionRepository sessionRepository,
                                    SpecPackActivityLogger activityLogger) {
        this.specPackRepository = specPackRepository;
        this.sessionRepository = sessionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentSessionResponse execute(StartAgentSessionCommand command) {
        specPackRepository.findById(command.specPackId())
                .filter(p -> p.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.specPackNotFound(command.specPackId()));

        SpecPackAgentSession session = SpecPackAgentSession.create(
                command.projectId(),
                command.specPackId(),
                command.scopePackageId()
        );
        SpecPackAgentSession saved = sessionRepository.save(session);

        for (AgentStageCode code : AgentStageCode.values()) {
            SpecPackAgentStage stage = SpecPackAgentStage.createInitial(saved.id(), code);
            sessionRepository.saveStage(stage);
        }

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_AGENT_SESSION,
                saved.id(),
                SpecPackActivityActions.AGENT_SESSION_STARTED,
                "Agent session started for spec pack: " + saved.specPackId()
        );

        return AgentSessionResponse.from(saved);
    }
}
