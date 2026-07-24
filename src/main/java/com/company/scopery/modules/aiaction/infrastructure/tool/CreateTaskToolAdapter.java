package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateTaskToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateTaskToolAdapter.class);

    private static final String TOOL_CODE = "create_task";
    private static final String TOOL_VERSION = "v1";

    private final TaskRepository taskRepository;
    private final ProjectPhaseRepository projectPhaseRepository;

    public CreateTaskToolAdapter(TaskRepository taskRepository,
                                  ProjectPhaseRepository projectPhaseRepository) {
        this.taskRepository = taskRepository;
        this.projectPhaseRepository = projectPhaseRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new task in the project. Use this when the user explicitly asks to create, add, or schedule a new task. "
                + "Required: projectId and title. Optional: description, priority (LOW/MEDIUM/HIGH/CRITICAL), dueDate (YYYY-MM-DD), "
                + "estimateHours, projectPhaseId. If projectPhaseId is not provided, the first active phase will be used.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project to create the task in."
                    },
                    "title": {
                      "type": "string",
                      "description": "The title of the task."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description of the task."
                    },
                    "priority": {
                      "type": "string",
                      "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"],
                      "description": "Task priority. Defaults to MEDIUM if not provided."
                    },
                    "dueDate": {
                      "type": "string",
                      "format": "date",
                      "description": "Due date in YYYY-MM-DD format. Optional."
                    },
                    "estimateHours": {
                      "type": "number",
                      "description": "Estimated effort in hours. Defaults to 1 if not provided."
                    },
                    "projectPhaseId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "UUID of the project phase. If omitted, the first active phase is used."
                    }
                  },
                  "required": ["projectId", "title"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String description = getString(inputArgs, "description");
        if (description != null) hints.put("description", description);
        String priority = getString(inputArgs, "priority");
        if (priority != null) hints.put("priority", priority);
        String phaseIdStr = getString(inputArgs, "projectPhaseId");
        if (phaseIdStr != null) {
            try {
                UUID phaseId = UUID.fromString(phaseIdStr);
                projectPhaseRepository.findById(phaseId)
                        .ifPresent(p -> hints.put("phaseTitle", p.name()));
            } catch (Exception ignored) {}
        }
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String title = getString(input, "title");
        if (projectId == null || title == null || title.isBlank()) {
            return new AiActionDryRunResult(false, List.of("Missing required field: projectId or title"),
                    null, false, null);
        }
        UUID projectUuid;
        try {
            projectUuid = UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid projectId format"), null, false, null);
        }
        ProjectPhase phase = resolvePhase(projectUuid, getString(input, "projectPhaseId"));
        if (phase == null) {
            return new AiActionDryRunResult(false, List.of("No active or planned phase found for project: " + projectId),
                    null, false, null);
        }
        String diffJson = "{\"projectId\":\"" + projectId + "\",\"title\":\"" + title
                + "\",\"phaseId\":\"" + phase.id() + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String title = getString(input, "title");

        if (projectId == null || title == null || title.isBlank()) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid;
        try {
            projectUuid = UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_PROJECT_ID", false);
        }

        ProjectPhase phase = resolvePhase(projectUuid, getString(input, "projectPhaseId"));
        if (phase == null) {
            return AiActionToolResult.failed("NO_ACTIVE_PHASE_FOUND", false);
        }

        String code = generateUniqueCode(projectUuid);
        String description = getString(input, "description");
        String priorityStr = getString(input, "priority");
        TaskPriority priority = priorityStr != null ? parseOrDefault(priorityStr) : TaskPriority.MEDIUM;

        LocalDate dueDate = null;
        String dueDateStr = getString(input, "dueDate");
        if (dueDateStr != null) {
            try {
                dueDate = LocalDate.parse(dueDateStr);
            } catch (Exception e) {
                log.warn("[CreateTaskTool] Invalid dueDate '{}', ignoring", dueDateStr);
            }
        }

        BigDecimal estimateHours = BigDecimal.ONE;
        Object estimateRaw = input.get("estimateHours");
        if (estimateRaw != null) {
            try {
                estimateHours = new BigDecimal(estimateRaw.toString());
                if (estimateHours.compareTo(BigDecimal.ZERO) <= 0) estimateHours = BigDecimal.ONE;
            } catch (Exception e) {
                log.warn("[CreateTaskTool] Invalid estimateHours '{}', using 1", estimateRaw);
            }
        }

        Task task = Task.create(
                projectUuid, phase.id(), null,
                code, title, description,
                null, null, null,
                estimateHours, null, dueDate,
                priority);

        Task saved = taskRepository.save(task);

        log.info("[CreateTaskTool] Task created — id={} code={} project={}", saved.id(), saved.code(), projectUuid);

        String resultToken = saved.id() + ":v1";
        String resultSummary = "{\"taskId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                + "\",\"title\":\"" + saved.title() + "\",\"phaseId\":\"" + phase.id() + "\"}";

        return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private ProjectPhase resolvePhase(UUID projectId, String phaseIdStr) {
        if (phaseIdStr != null) {
            try {
                UUID phaseId = UUID.fromString(phaseIdStr);
                return projectPhaseRepository.findById(phaseId).orElse(null);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        List<ProjectPhase> phases = projectPhaseRepository.findAllByProjectId(projectId);
        return phases.stream()
                .filter(p -> p.status() == ProjectPhaseStatus.ACTIVE)
                .findFirst()
                .or(() -> phases.stream()
                        .filter(p -> p.status() == ProjectPhaseStatus.PLANNED)
                        .findFirst())
                .orElse(null);
    }

    private String generateUniqueCode(UUID projectId) {
        for (int i = 0; i < 10; i++) {
            String candidate = "TSK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!taskRepository.existsByProjectIdAndCode(projectId, candidate)) {
                return candidate;
            }
        }
        return "TSK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private static TaskPriority parseOrDefault(String value) {
        try {
            return TaskPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TaskPriority.MEDIUM;
        }
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
