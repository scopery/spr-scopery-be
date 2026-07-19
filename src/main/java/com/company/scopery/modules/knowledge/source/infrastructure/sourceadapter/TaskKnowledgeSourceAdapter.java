package com.company.scopery.modules.knowledge.source.infrastructure.sourceadapter;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class TaskKnowledgeSourceAdapter {

    private final TaskRepository tasks;
    private final ProjectRepository projects;

    public TaskKnowledgeSourceAdapter(TaskRepository tasks, ProjectRepository projects) {
        this.tasks = tasks;
        this.projects = projects;
    }

    public Optional<KnowledgeSourceSnapshot> buildSnapshot(UUID taskId) {
        return tasks.findById(taskId).map(task -> {
            var project = projects.findById(task.projectId()).orElse(null);
            UUID workspaceId = project != null ? project.workspaceId() : null;
            if (workspaceId == null) return null;

            String normalizedText = buildNormalizedText(task);
            List<String> aclTokens = buildAclTokens(workspaceId, task.projectId(), task.inChargeUserId());

            return new KnowledgeSourceSnapshot(
                    workspaceId,
                    task.projectId(),
                    KnowledgeSourceType.TASK,
                    taskId,
                    stableVersionRef(taskId, task.version()),
                    task.title(),
                    "und",
                    "INTERNAL",
                    normalizedText,
                    Map.of(
                            "status", task.status() != null ? task.status().name() : "",
                            "assigneeUserId", task.inChargeUserId() != null ? task.inChargeUserId().toString() : null,
                            "wbsNodeId", task.wbsNodeId() != null ? task.wbsNodeId().toString() : null,
                            "dueDate", task.dueDate() != null ? task.dueDate().toString() : null,
                            "priority", task.priority() != null ? task.priority().name() : null
                    ),
                    aclTokens,
                    String.valueOf(task.version()),
                    "/tasks/" + taskId,
                    task.updatedAt()
            );
        });
    }

    private String buildNormalizedText(Task task) {
        var sb = new StringBuilder();
        appendSection(sb, "Title", task.title());
        if (task.status() != null) appendSection(sb, "Status", task.status().name());
        appendSection(sb, "Description", task.description());
        if (task.dueDate() != null) appendSection(sb, "Dates", "Due: " + task.dueDate());
        return sb.toString().strip();
    }

    private void appendSection(StringBuilder sb, String heading, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(heading).append("\n").append(value).append("\n\n");
        }
    }

    private List<String> buildAclTokens(UUID workspaceId, UUID projectId, UUID assigneeUserId) {
        List<String> tokens = new ArrayList<>();
        tokens.add("workspace:" + workspaceId);
        tokens.add("project:" + projectId);
        if (assigneeUserId != null) tokens.add("user:" + assigneeUserId);
        tokens.sort(String::compareTo);
        return List.copyOf(tokens);
    }

    private UUID stableVersionRef(UUID taskId, int version) {
        // UUID v5-style: deterministic from taskId + version
        return UUID.nameUUIDFromBytes(("TASK:" + taskId + ":" + version).getBytes());
    }
}
