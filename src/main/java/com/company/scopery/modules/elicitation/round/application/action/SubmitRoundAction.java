package com.company.scopery.modules.elicitation.round.application.action;

import com.company.scopery.modules.elicitation.round.application.command.SubmitRoundCommand;
import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.domain.enums.RoundStatus;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.shared.activity.ElicitationActivityLogger;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationActivityActions;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationEntityTypes;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SubmitRoundAction {

    private final ElicitationRoundRepository roundRepository;
    private final ElicitationActivityLogger activityLogger;

    public SubmitRoundAction(ElicitationRoundRepository roundRepository,
                              ElicitationActivityLogger activityLogger) {
        this.roundRepository = roundRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ElicitationRoundResponse execute(SubmitRoundCommand command) {
        ElicitationRound round = roundRepository.findById(command.roundId())
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(command.roundId()));

        if (round.status() == RoundStatus.SUBMITTED || round.status() == RoundStatus.PROCESSING
                || round.status() == RoundStatus.COMPLETED) {
            throw ElicitationExceptions.roundAlreadySubmitted(round.id());
        }

        round.submit();
        ElicitationRound saved = roundRepository.save(round);

        activityLogger.logSuccess(
                ElicitationEntityTypes.ROUND,
                saved.id(),
                ElicitationActivityActions.SUBMIT_ROUND,
                "Elicitation round submitted: round " + saved.roundNumber()
        );

        return ElicitationRoundResponse.from(saved);
    }
}
