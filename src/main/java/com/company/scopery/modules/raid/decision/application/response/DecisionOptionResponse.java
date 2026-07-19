package com.company.scopery.modules.raid.decision.application.response;

import com.company.scopery.modules.raid.decision.domain.model.DecisionOption;
import java.util.UUID;

public record DecisionOptionResponse(
        UUID id,
        UUID decisionId,
        String optionTitle,
        String optionDescription,
        String pros,
        String cons,
        String estimatedImpact,
        boolean selectedFlag
) {
    public static DecisionOptionResponse from(DecisionOption o) {
        return new DecisionOptionResponse(o.id(), o.decisionId(), o.optionTitle(), o.optionDescription(),
                o.pros(), o.cons(), o.estimatedImpact(), o.selectedFlag());
    }
}
