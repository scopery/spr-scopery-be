package com.company.scopery.modules.specpack.clarification.application.action;

import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.clarification.application.command.CreateClarificationCommand;
import com.company.scopery.modules.specpack.clarification.application.response.ClarificationResponse;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationPriority;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationSource;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarification;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarificationRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ImportClarificationsAction {

    private final SpecPackAgentSessionRepository sessionRepository;
    private final SpecPackClarificationRepository clarificationRepository;
    private final SpecPackActivityLogger activityLogger;

    public ImportClarificationsAction(SpecPackAgentSessionRepository sessionRepository,
                                       SpecPackClarificationRepository clarificationRepository,
                                       SpecPackActivityLogger activityLogger) {
        this.sessionRepository = sessionRepository;
        this.clarificationRepository = clarificationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public List<ClarificationResponse> execute(List<CreateClarificationCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return List.of();
        }

        CreateClarificationCommand first = commands.get(0);
        SpecPackAgentSession session = sessionRepository.findById(first.sessionId())
                .filter(s -> s.projectId().equals(first.projectId()))
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(first.sessionId()));

        if (session.status() != AgentSessionStatus.ACTIVE) {
            throw SpecPackExceptions.agentSessionNotActive(first.sessionId());
        }

        List<ClarificationResponse> results = commands.stream().map(command -> {
            ClarificationPriority priority = SpecPackEnumParser.parseRequired(
                    ClarificationPriority.class, command.priority(), "priority");
            ClarificationSource source = SpecPackEnumParser.parseRequired(
                    ClarificationSource.class, command.source(), "source");

            SpecPackClarification clarification = SpecPackClarification.create(
                    command.sessionId(),
                    command.code(),
                    command.question(),
                    priority,
                    source
            );

            SpecPackClarification saved = clarificationRepository.save(clarification);

            activityLogger.logSuccess(
                    SpecPackEntityTypes.SPEC_PACK_CLARIFICATION,
                    saved.id(),
                    SpecPackActivityActions.CLARIFICATION_CREATED,
                    "Clarification imported: " + saved.code()
            );

            return ClarificationResponse.from(saved);
        }).toList();

        return results;
    }
}
