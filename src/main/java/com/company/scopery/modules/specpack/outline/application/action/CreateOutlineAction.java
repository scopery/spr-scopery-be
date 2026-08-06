package com.company.scopery.modules.specpack.outline.application.action;

import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.outline.application.command.CreateOutlineCommand;
import com.company.scopery.modules.specpack.outline.application.response.OutlineResponse;
import com.company.scopery.modules.specpack.outline.domain.enums.OutlineStatus;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutline;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutlineRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateOutlineAction {

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackOutlineRepository outlineRepository;
    private final SpecPackActivityLogger activityLogger;

    public CreateOutlineAction(SpecPackAgentSessionRepository sessionRepository,
                                SpecPackOutlineRepository outlineRepository,
                                SpecPackActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.outlineRepository = outlineRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OutlineResponse execute(CreateOutlineCommand command) {
        SpecPackAgentSession session = sessionRepository.findById(command.sessionId())
                .filter(s -> s.projectId().equals(command.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(command.sessionId()));

        if (session.status() != AgentSessionStatus.ACTIVE) {
            throw SpecPackExceptions.agentSessionNotActive(command.sessionId());
        }

        List<SpecPackOutline> existing = outlineRepository.findAllBySessionId(command.sessionId());
        int maxVersion = existing.stream().mapToInt(SpecPackOutline::versionNumber).max().orElse(0);

        for (SpecPackOutline outline : existing) {
            if (outline.status() == OutlineStatus.DRAFT || outline.status() == OutlineStatus.APPROVED) {
                outline.supersede();
                outlineRepository.save(outline);
            }
        }

        SpecPackOutline newOutline = SpecPackOutline.create(
                command.sessionId(),
                maxVersion + 1,
                command.outlineJson()
        );

        SpecPackOutline saved = outlineRepository.save(newOutline);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_OUTLINE,
                saved.id(),
                SpecPackActivityActions.OUTLINE_CREATED,
                "Outline v" + saved.versionNumber() + " created for session: " + saved.sessionId()
        );

        return OutlineResponse.from(saved);
    }
}
