package com.company.scopery.modules.airecommendation.application.detector;

import com.company.scopery.modules.airecommendation.application.port.RecommendationDetector;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class TaskBlockedWithoutMitigationDetector implements RecommendationDetector {

    private static final String DETECTOR_CODE = "TASK_BLOCKED_WITHOUT_MITIGATION";
    private static final String SUGGESTION_TYPE = "TASK_BLOCKED_WITHOUT_MITIGATION";
    private static final BigDecimal CONFIDENCE = new BigDecimal("0.9500");

    private final TaskRepository taskRepository;

    public TaskBlockedWithoutMitigationDetector(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String detectorCode() {
        return DETECTOR_CODE;
    }

    @Override
    public List<SuggestionCandidate> detect(DetectorContext ctx) {
        return taskRepository.findAllByProjectId(ctx.projectId()).stream()
                .filter(task -> task.status() == TaskStatus.BLOCKED)
                .map(this::toCandidate)
                .toList();
    }

    private SuggestionCandidate toCandidate(Task task) {
        return new SuggestionCandidate(
                SUGGESTION_TYPE,
                "TASK_BLOCKED_WITHOUT_MITIGATION_V1",
                1,
                "TASK",
                task.id(),
                Map.of("taskId", task.id().toString(), "taskCode", task.code(),
                        "mitigationText", (Object) null,
                        "reviewDate", (Object) null,
                        "proposalMode", "MANUAL_INPUT_REQUIRED"),
                "Add mitigation plan to blocked task: " + task.title(),
                "Task \"" + task.code() + " " + task.title() + "\" is blocked with no mitigation plan.",
                "Blocked tasks without a resolution plan delay project delivery.",
                "QUALITY",
                "MEDIUM",
                CONFIDENCE,
                ConfidenceMethod.HEURISTIC,
                List.of(new EvidenceFact("TASK", task.id(),
                        task.title(), "Status: BLOCKED — no mitigation recorded",
                        "/projects/" + task.projectId() + "/tasks/" + task.id()))
        );
    }
}
