package com.company.scopery.modules.aiplanning.context.application.service;

import com.company.scopery.modules.aiplanning.contextsnapshot.domain.model.AiPlanningContextSnapshot;
import com.company.scopery.modules.aiplanning.shared.authorization.AiPlanningAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AiPlanningContextBuilder {
    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final WbsNodeRepository wbsNodes;
    private final TaskRepository tasks;
    private final AiPlanningAuthorizationService authorization;
    private final ObjectMapper objectMapper;

    public AiPlanningContextBuilder(ProjectRepository projects,
                                    ProjectPhaseRepository phases,
                                    WbsNodeRepository wbsNodes,
                                    TaskRepository tasks,
                                    AiPlanningAuthorizationService authorization,
                                    ObjectMapper objectMapper) {
        this.projects = projects;
        this.phases = phases;
        this.wbsNodes = wbsNodes;
        this.tasks = tasks;
        this.authorization = authorization;
        this.objectMapper = objectMapper;
    }

    public AiPlanningContextSnapshot build(UUID projectId, UUID actorUserId, List<String> requestedSections, String traceId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));

        List<String> included = new ArrayList<>();
        Map<String, Object> redactions = new LinkedHashMap<>();
        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> accessScope = new LinkedHashMap<>();

        List<String> sections = requestedSections == null || requestedSections.isEmpty()
                ? List.of("PROJECT", "PHASES", "WBS", "TASKS")
                : requestedSections;

        if (sections.contains("PROJECT")) {
            included.add("PROJECT");
            payload.put("project", Map.of(
                    "id", project.id(),
                    "code", project.code() == null ? "" : project.code(),
                    "name", project.name(),
                    "status", project.status().name(),
                    "description", project.description() == null ? "" : project.description()));
        }
        if (sections.contains("PHASES")) {
            included.add("PHASES");
            List<Map<String, Object>> phaseRows = new ArrayList<>();
            for (ProjectPhase phase : phases.findAllByProjectId(projectId)) {
                phaseRows.add(Map.of(
                        "id", phase.id(),
                        "name", phase.name(),
                        "status", phase.status().name()));
            }
            payload.put("phases", phaseRows);
        }
        if (sections.contains("WBS")) {
            included.add("WBS");
            List<Map<String, Object>> wbsRows = new ArrayList<>();
            for (WbsNode node : wbsNodes.findAllByProjectId(projectId)) {
                wbsRows.add(Map.of(
                        "id", node.id(),
                        "title", node.title(),
                        "type", node.nodeType().name(),
                        "phaseId", node.projectPhaseId() == null ? "" : node.projectPhaseId()));
            }
            payload.put("wbs", summarize(wbsRows, 100));
        }
        if (sections.contains("TASKS")) {
            included.add("TASKS");
            List<Map<String, Object>> taskRows = new ArrayList<>();
            for (var task : tasks.findAllByProjectId(projectId)) {
                taskRows.add(Map.of(
                        "id", task.id(),
                        "title", task.title(),
                        "status", task.status().name(),
                        "estimateHours", task.estimateHours() == null ? 0 : task.estimateHours()));
            }
            payload.put("tasks", summarize(taskRows, 150));
        }
        if (sections.contains("FINANCE_SUMMARY")) {
            if (authorization.canViewFinance(projectId)) {
                included.add("FINANCE_SUMMARY");
                payload.put("financeSummary", Map.of("available", true, "detailsRedacted", false));
            } else {
                redactions.put("FINANCE_SUMMARY", "PROJECT_FINANCE_VIEW_REQUIRED");
                payload.put("financeSummary", Map.of("financeAvailable", true, "detailsRedacted", true,
                        "reason", "PROJECT_FINANCE_VIEW_REQUIRED"));
            }
        }
        if (sections.contains("QUOTE_SUMMARY")) {
            if (authorization.canViewQuote(projectId)) {
                included.add("QUOTE_SUMMARY");
                payload.put("quoteSummary", Map.of("available", true, "detailsRedacted", false));
            } else {
                redactions.put("QUOTE_SUMMARY", "QUOTE_VIEW_REQUIRED");
                payload.put("quoteSummary", Map.of("quoteAvailable", true, "detailsRedacted", true,
                        "reason", "QUOTE_VIEW_REQUIRED"));
            }
        }

        accessScope.put("actorUserId", actorUserId);
        accessScope.put("projectId", projectId);
        accessScope.put("canViewFinance", authorization.canViewFinance(projectId));
        accessScope.put("canViewQuote", authorization.canViewQuote(projectId));

        return AiPlanningContextSnapshot.create(
                project.id(), project.workspaceId(), actorUserId, "PROJECT_PLANNING",
                json(accessScope), json(included), json(redactions), json(payload),
                estimateTokens(payload), traceId);
    }

    private List<Map<String, Object>> summarize(List<Map<String, Object>> rows, int max) {
        if (rows.size() <= max) return rows;
        return rows.subList(0, max);
    }

    private int estimateTokens(Map<String, Object> payload) {
        return Math.max(1, json(payload).length() / 4);
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
