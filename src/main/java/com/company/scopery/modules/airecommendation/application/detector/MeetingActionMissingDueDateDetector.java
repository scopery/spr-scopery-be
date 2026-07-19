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
public class MeetingActionMissingDueDateDetector implements RecommendationDetector {

    private static final Set<ActionItemStatus> ACTIVE_STATUSES =
            Set.of(ActionItemStatus.OPEN, ActionItemStatus.IN_PROGRESS);
    private static final String DETECTOR_CODE = "MEETING_ACTION_MISSING_DUE_DATE";
    private static final String SUGGESTION_TYPE = "MEETING_ACTION_MISSING_DUE_DATE";

    private final MeetingActionItemRepository actionItemRepository;

    public MeetingActionMissingDueDateDetector(MeetingActionItemRepository actionItemRepository) {
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
                .filter(item -> item.dueDate() == null)
                .map(this::toCandidate)
                .toList();
    }

    private SuggestionCandidate toCandidate(MeetingActionItem item) {
        return new SuggestionCandidate(
                SUGGESTION_TYPE,
                "MEETING_ACTION_MISSING_DUE_DATE_V1",
                1,
                "MEETING_ACTION_ITEM",
                item.id(),
                Map.of("actionItemId", item.id().toString(),
                        "proposedDueDate", (Object) null),
                "Set due date for action item: " + item.title(),
                "Meeting action item \"" + item.title() + "\" has no due date.",
                "Action items without due dates cannot be tracked for timely completion.",
                "PLANNING",
                "LOW",
                BigDecimal.ONE,
                ConfidenceMethod.DETERMINISTIC,
                List.of(new EvidenceFact("MEETING_ACTION_ITEM", item.id(),
                        item.title(), "Due date: not set",
                        "/projects/" + item.projectId() + "/action-items/" + item.id()))
        );
    }
}
