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
public class TaskMissingOwnerDetector implements RecommendationDetector {

    private static final Set<TaskStatus> ACTIVE_STATUSES =
            Set.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.BLOCKED);
    private static final String DETECTOR_CODE = "TASK_MISSING_OWNER";
    private static final String SUGGESTION_TYPE = "TASK_MISSING_OWNER";

    private final TaskRepository taskRepository;

    public TaskMissingOwnerDetector(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String detectorCode() {
        return DETECTOR_CODE;
    }

    @Override
    public List<SuggestionCandidate> detect(DetectorContext ctx) {
        return taskRepository.findAllByProjectId(ctx.projectId()).stream()
                .filter(task -> ACTIVE_STATUSES.contains(task.status()))
                .filter(task -> task.inChargeUserId() == null)
                .map(this::toCandidate)
                .toList();
    }

    private SuggestionCandidate toCandidate(Task task) {
        return new SuggestionCandidate(
                SUGGESTION_TYPE,
                "TASK_MISSING_OWNER_V1",
                1,
                "TASK",
                task.id(),
                Map.of("taskId", task.id().toString(), "taskCode", task.code(),
                        "candidateUserId", (Object) null,
                        "assignmentReasonCode", "MANUAL_SELECTION_REQUIRED"),
                "Assign owner to task: " + task.title(),
                "Task \"" + task.code() + " " + task.title() + "\" has no owner assigned.",
                "Tasks without an assigned owner may stall and miss deadlines.",
                "PLANNING",
                "LOW",
                BigDecimal.ONE,
                ConfidenceMethod.DETERMINISTIC,
                List.of(new EvidenceFact("TASK", task.id(),
                        task.title(), "Owner: not assigned",
                        "/projects/" + task.projectId() + "/tasks/" + task.id()))
        );
    }
}
