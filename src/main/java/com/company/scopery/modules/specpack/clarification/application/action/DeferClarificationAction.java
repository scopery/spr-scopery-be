package com.company.scopery.modules.specpack.clarification.application.action;

import com.company.scopery.modules.specpack.clarification.application.command.DeferClarificationCommand;
import com.company.scopery.modules.specpack.clarification.application.response.ClarificationResponse;
import com.company.scopery.modules.specpack.clarification.domain.enums.ClarificationStatus;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarification;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarificationRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeferClarificationAction {

    private final SpecPackClarificationRepository clarificationRepository;
    private final SpecPackActivityLogger activityLogger;

    public DeferClarificationAction(SpecPackClarificationRepository clarificationRepository,
                                     SpecPackActivityLogger activityLogger) {
        this.clarificationRepository = clarificationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ClarificationResponse execute(DeferClarificationCommand command) {
        SpecPackClarification clarification = clarificationRepository.findById(command.clarificationId())
                .filter(c -> c.sessionId().equals(command.sessionId()))
                .orElseThrow(() -> SpecPackExceptions.clarificationNotFound(command.clarificationId()));

        if (clarification.status() != ClarificationStatus.OPEN) {
            throw SpecPackExceptions.clarificationNotOpen(command.clarificationId());
        }

        clarification.defer();
        SpecPackClarification saved = clarificationRepository.save(clarification);

        activityLogger.logSuccess(
                SpecPackEntityTypes.SPEC_PACK_CLARIFICATION,
                saved.id(),
                SpecPackActivityActions.CLARIFICATION_DEFERRED,
                "Clarification deferred: " + saved.code()
        );

        return ClarificationResponse.from(saved);
    }
}
