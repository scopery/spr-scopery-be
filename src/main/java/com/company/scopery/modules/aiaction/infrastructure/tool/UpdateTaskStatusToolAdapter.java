package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UpdateTaskStatusToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(UpdateTaskStatusToolAdapter.class);

    private static final String TOOL_CODE = "update_task_status";
    private static final String TOOL_VERSION = "v1";

    private final TaskRepository taskRepository;

    public UpdateTaskStatusToolAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Update the status of a task. Use this when the user explicitly asks to start, complete, cancel, or block a task. "
                + "Valid statuses: TODO, IN_PROGRESS, DONE, CANCELLED, BLOCKED.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "taskId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the task to update."
                    },
                    "newStatus": {
                      "type": "string",
                      "enum": ["TODO", "IN_PROGRESS", "DONE", "CANCELLED", "BLOCKED"],
                      "description": "The new status to assign to the task."
                    }
                  },
                  "required": ["taskId", "newStatus"]
                }
                """;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String taskId = getString(input, "taskId");
        String newStatus = getString(input, "newStatus");
        if (taskId == null || newStatus == null) {
            return new AiActionDryRunResult(false, List.of("Missing required field: taskId or newStatus"),
                    null, false, null);
        }
        Task task = taskRepository.findById(UUID.fromString(taskId)).orElse(null);
        if (task == null) {
            return new AiActionDryRunResult(false, List.of("Task not found: " + taskId),
                    null, false, null);
        }
        String diffJson = "{\"taskId\":\"" + taskId + "\",\"currentStatus\":\"" + task.status()
                + "\",\"newStatus\":\"" + newStatus + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String taskId = getString(input, "taskId");
        String newStatusStr = getString(input, "newStatus");
        if (taskId == null || newStatusStr == null) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID taskUuid;
        try {
            taskUuid = UUID.fromString(taskId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_TASK_ID", false);
        }

        TaskStatus newStatus;
        try {
            newStatus = TaskStatus.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_STATUS_VALUE", false);
        }

        Task task = taskRepository.findById(taskUuid).orElse(null);
        if (task == null) {
            return AiActionToolResult.failed("TASK_NOT_FOUND", false);
        }

        TaskStatus previousStatus = task.status();

        Task updated = new Task(
                task.id(), task.projectId(), task.projectPhaseId(), task.wbsNodeId(),
                task.code(), task.title(), task.description(),
                task.inChargeUserId(), task.plannedRoleCode(), task.plannedRoleName(),
                task.estimateHours(), task.plannedStartDate(), task.dueDate(),
                task.priority(), newStatus,
                task.startedAt(), task.startedBy(),
                task.blockedAt(), task.completedAt(), task.completedBy(),
                task.cancelledAt(), task.cancelledBy(),
                task.archivedAt(), task.archivedBy(),
                task.version() + 1, task.createdAt(), java.time.Instant.now());

        taskRepository.save(updated);

        log.info("[UpdateTaskStatusTool] Task {} status changed {} → {}", taskUuid, previousStatus, newStatus);

        String resultVersionToken = taskUuid + ":v" + updated.version();
        String resultSummary = "{\"taskId\":\"" + taskUuid + "\",\"previousStatus\":\"" + previousStatus
                + "\",\"newStatus\":\"" + newStatus + "\"}";

        return AiActionToolResult.succeeded(resultVersionToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        // Compensation not supported — status changes are reversible manually
        return AiActionCompensationResult.unsupported();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
