package com.company.scopery.modules.airecommendation.application.detector;

import com.company.scopery.modules.airecommendation.application.port.RecommendationDetector;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItem;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItemRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MeetingActionMissingOwnerDetector implements RecommendationDetector {

    private static final Set<ActionItemStatus> ACTIVE_STATUSES =
            Set.of(ActionItemStatus.OPEN, ActionItemStatus.IN_PROGRESS, ActionItemStatus.OVERDUE);
    private static final String DETECTOR_CODE = "MEETING_ACTION_MISSING_OWNER";
    private static final String SUGGESTION_TYPE = "MEETING_ACTION_MISSING_OWNER";

    private final MeetingActionItemRepository actionItemRepository;

    public MeetingActionMissingOwnerDetector(MeetingActionItemRepository actionItemRepository) {
        this.actionItemRepository = actionItemRepository;
    }

    @Override
    public String detectorCode() {
        return DETECTOR_CODE;
    }

    @Override
    public List<SuggestionCandidate> detect(DetectorContext ctx) {
        return actionItemRepository.findByProjectId(ctx.projectId()).stream()
                .filter(item -> ACTIVE_STATUSES.contains(item.status()))
                .filter(item -> item.ownerTargetId() == null)
                .map(this::toCandidate)
                .toList();
    }

    private SuggestionCandidate toCandidate(MeetingActionItem item) {
        return new SuggestionCandidate(
                SUGGESTION_TYPE,
                "MEETING_ACTION_MISSING_OWNER_V1",
                1,
                "MEETING_ACTION_ITEM",
                item.id(),
                Map.of("actionItemId", item.id().toString(),
                        "candidateUserId", (Object) null,
                        "proposalMode", "MANUAL_SELECTION_REQUIRED"),
                "Assign owner to action item: " + item.title(),
                "Meeting action item \"" + item.title() + "\" has no owner assigned.",
                "Action items without owners are unlikely to be completed on time.",
                "PLANNING",
                "LOW",
                BigDecimal.ONE,
                ConfidenceMethod.DETERMINISTIC,
                List.of(new EvidenceFact("MEETING_ACTION_ITEM", item.id(),
                        item.title(), "Owner: not assigned",
                        "/projects/" + item.projectId() + "/action-items/" + item.id()))
        );
    }
}
