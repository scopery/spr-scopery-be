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
import java.util.Set;

@Component
public class TaskMissingEstimateDetector implements RecommendationDetector {

    private static final Set<TaskStatus> SCHEDULABLE_STATUSES =
            Set.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS);
    private static final String DETECTOR_CODE = "TASK_MISSING_ESTIMATE";
    private static final String SUGGESTION_TYPE = "TASK_MISSING_ESTIMATE";

    private final TaskRepository taskRepository;

    public TaskMissingEstimateDetector(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String detectorCode() {
        return DETECTOR_CODE;
    }

    @Override
    public List<SuggestionCandidate> detect(DetectorContext ctx) {
        return taskRepository.findAllByProjectId(ctx.projectId()).stream()
                .filter(task -> SCHEDULABLE_STATUSES.contains(task.status()))
                .filter(this::isMissingEstimate)
                .map(this::toCandidate)
                .toList();
    }

    private boolean isMissingEstimate(Task task) {
        return task.estimateHours() == null
                || task.estimateHours().compareTo(BigDecimal.ZERO) <= 0;
    }

    private SuggestionCandidate toCandidate(Task task) {
        return new SuggestionCandidate(
                SUGGESTION_TYPE,
                "TASK_MISSING_ESTIMATE_V1",
                1,
                "TASK",
                task.id(),
                Map.of("taskId", task.id().toString(), "taskCode", task.code(),
                        "estimateValue", (Object) null,
                        "estimateUnit", "UNKNOWN",
                        "estimationMethod", "MISSING_FIELD_ONLY"),
                "Add estimate to task: " + task.title(),
                "Task \"" + task.code() + " " + task.title() + "\" has no time estimate.",
                "Tasks without estimates cannot be scheduled accurately.",
                "PLANNING",
                "LOW",
                BigDecimal.ONE,
                ConfidenceMethod.DETERMINISTIC,
                List.of(new EvidenceFact("TASK", task.id(),
                        task.title(), "Estimate hours: not set",
                        "/projects/" + task.projectId() + "/tasks/" + task.id()))
        );
    }
}
