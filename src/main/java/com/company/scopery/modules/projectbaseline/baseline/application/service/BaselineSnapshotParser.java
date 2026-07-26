package com.company.scopery.modules.projectbaseline.baseline.application.service;

import com.company.scopery.modules.projectbaseline.baseline.application.response.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class BaselineSnapshotParser {

    private static final Logger log = LoggerFactory.getLogger(BaselineSnapshotParser.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public BaselineSnapshotParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BaselineSummaryDto parseSummary(String summaryJson, String snapshotJson) {
        int phaseCount = 0;
        int wbsCount = 0;
        int taskCount = 0;
        int dependencyCount = 0;
        int milestoneCount = 0;
        String plannedStartDate = null;
        String plannedEndDate = null;
        BigDecimal estimateHours = null;
        BigDecimal revenue = null;
        BigDecimal directCost = null;
        BigDecimal overhead = null;
        BigDecimal grossMargin = null;
        BigDecimal pbt = null;
        String currencyCode = null;
        BigDecimal totalQuotedAmount = null;
        BigDecimal targetMarginPercent = null;

        if (summaryJson != null && !summaryJson.isBlank()) {
            try {
                Map<String, Object> summary = objectMapper.readValue(summaryJson, MAP_TYPE);
                phaseCount = toInt(summary.getOrDefault("phaseCount", 0));
                wbsCount = toInt(summary.getOrDefault("wbsCount", 0));
                taskCount = toInt(summary.getOrDefault("taskCount", 0));
                dependencyCount = toInt(summary.getOrDefault("dependencyCount", 0));
                milestoneCount = toInt(summary.getOrDefault("milestoneCount", 0));

                Object financeObj = summary.get("finance");
                if (financeObj instanceof Map<?, ?> financeMap) {
                    revenue = toBigDecimal(financeMap.get("plannedRevenue"));
                    directCost = toBigDecimal(financeMap.get("directCost"));
                    overhead = toBigDecimal(financeMap.get("overhead"));
                    grossMargin = toBigDecimal(financeMap.get("grossMargin"));
                    pbt = toBigDecimal(financeMap.get("pbt"));
                    currencyCode = toStr(financeMap.get("currencyCode"));
                }

                Object quoteObj = summary.get("quote");
                if (quoteObj instanceof Map<?, ?> quoteMap) {
                    totalQuotedAmount = toBigDecimal(quoteMap.get("totalQuotedAmount"));
                    targetMarginPercent = toBigDecimal(quoteMap.get("targetMarginPercent"));
                    if (currencyCode == null) {
                        currencyCode = toStr(quoteMap.get("currencyCode"));
                    }
                }
            } catch (Exception e) {
                log.warn("BaselineSnapshotParser: failed to parse summaryJson: {}", e.getMessage());
            }
        }

        if (snapshotJson != null && !snapshotJson.isBlank()) {
            try {
                Map<String, Object> snapshot = objectMapper.readValue(snapshotJson, MAP_TYPE);

                Object projectObj = snapshot.get("project");
                if (projectObj instanceof Map<?, ?> projectMap) {
                    plannedStartDate = toStr(projectMap.get("plannedStartDate"));
                    plannedEndDate = toStr(projectMap.get("plannedEndDate"));
                    if (plannedStartDate != null && plannedStartDate.isBlank()) plannedStartDate = null;
                    if (plannedEndDate != null && plannedEndDate.isBlank()) plannedEndDate = null;
                }

                Object tasksObj = snapshot.get("tasks");
                if (tasksObj instanceof List<?> taskList) {
                    BigDecimal sum = BigDecimal.ZERO;
                    for (Object t : taskList) {
                        if (t instanceof Map<?, ?> taskMap) {
                            BigDecimal eh = toBigDecimal(taskMap.get("estimateHours"));
                            if (eh != null) sum = sum.add(eh);
                        }
                    }
                    estimateHours = sum.compareTo(BigDecimal.ZERO) == 0 ? null : sum;
                }
            } catch (Exception e) {
                log.warn("BaselineSnapshotParser: failed to parse snapshotJson for summary: {}", e.getMessage());
            }
        }

        return new BaselineSummaryDto(phaseCount, wbsCount, taskCount, dependencyCount, milestoneCount,
                plannedStartDate, plannedEndDate, estimateHours,
                revenue, directCost, overhead, grossMargin, pbt, currencyCode,
                totalQuotedAmount, targetMarginPercent);
    }

    public List<BaselineTreeNodeDto> parseProjectTree(String snapshotJson) {
        if (snapshotJson == null || snapshotJson.isBlank()) return List.of();
        try {
            Map<String, Object> snapshot = objectMapper.readValue(snapshotJson, MAP_TYPE);

            List<Map<String, Object>> phaseList = toListOfMaps(snapshot.get("phases"));
            List<Map<String, Object>> wbsList = toListOfMaps(snapshot.get("wbs"));
            List<Map<String, Object>> taskList = toListOfMaps(snapshot.get("tasks"));

            // Index wbs by phase id
            Map<String, List<Map<String, Object>>> wbsByPhaseId = new LinkedHashMap<>();
            for (Map<String, Object> wbs : wbsList) {
                String phaseId = toStr(wbs.get("projectPhaseId"));
                if (phaseId == null) continue;
                wbsByPhaseId.computeIfAbsent(phaseId, k -> new ArrayList<>()).add(wbs);
            }

            // Index tasks by wbs node id
            Map<String, List<Map<String, Object>>> tasksByWbsId = new LinkedHashMap<>();
            for (Map<String, Object> task : taskList) {
                String wbsId = toStr(task.get("wbsNodeId"));
                if (wbsId == null) continue;
                tasksByWbsId.computeIfAbsent(wbsId, k -> new ArrayList<>()).add(task);
            }

            List<BaselineTreeNodeDto> tree = new ArrayList<>();
            for (Map<String, Object> phase : phaseList) {
                UUID phaseId = toUuid(phase.get("id"));
                String phaseCode = toStr(phase.get("code"));
                String phaseName = toStr(phase.get("name"));
                Map<String, Object> phaseMeta = new LinkedHashMap<>();
                phaseMeta.put("status", toStr(phase.get("status")));
                phaseMeta.put("displayOrder", phase.get("displayOrder"));

                List<BaselineTreeNodeDto> wbsChildren = new ArrayList<>();
                String phaseIdStr = phaseId != null ? phaseId.toString() : null;
                List<Map<String, Object>> phaseWbsNodes = phaseIdStr != null
                        ? wbsByPhaseId.getOrDefault(phaseIdStr, List.of()) : List.of();

                for (Map<String, Object> wbs : phaseWbsNodes) {
                    // Only include root wbs nodes (parentId == null)
                    if (wbs.get("parentId") != null && !wbs.get("parentId").toString().isBlank()
                            && !"null".equals(wbs.get("parentId").toString())) {
                        continue;
                    }
                    UUID wbsId = toUuid(wbs.get("id"));
                    String wbsCode = toStr(wbs.get("code"));
                    String wbsTitle = toStr(wbs.get("title"));
                    Map<String, Object> wbsMeta = new LinkedHashMap<>();
                    wbsMeta.put("status", toStr(wbs.get("status")));
                    wbsMeta.put("sortOrder", wbs.get("sortOrder"));
                    wbsMeta.put("parentId", wbs.get("parentId"));

                    List<BaselineTreeNodeDto> taskChildren = new ArrayList<>();
                    String wbsIdStr = wbsId != null ? wbsId.toString() : null;
                    List<Map<String, Object>> wbsTasks = wbsIdStr != null
                            ? tasksByWbsId.getOrDefault(wbsIdStr, List.of()) : List.of();
                    for (Map<String, Object> task : wbsTasks) {
                        UUID taskId = toUuid(task.get("id"));
                        String taskCode = toStr(task.get("code"));
                        String taskTitle = toStr(task.get("title"));
                        Map<String, Object> taskMeta = new LinkedHashMap<>();
                        taskMeta.put("status", toStr(task.get("status")));
                        taskMeta.put("estimateHours", task.get("estimateHours"));
                        taskMeta.put("dueDate", task.get("dueDate"));
                        taskChildren.add(new BaselineTreeNodeDto(taskId, "TASK", taskCode, taskTitle, taskMeta, List.of()));
                    }
                    wbsChildren.add(new BaselineTreeNodeDto(wbsId, "WBS", wbsCode, wbsTitle, wbsMeta, taskChildren));
                }
                tree.add(new BaselineTreeNodeDto(phaseId, "PHASE", phaseCode, phaseName, phaseMeta, wbsChildren));
            }
            return tree;
        } catch (Exception e) {
            log.warn("BaselineSnapshotParser: failed to parse project tree from snapshotJson: {}", e.getMessage());
            return List.of();
        }
    }

    public BaselineHealthDto parseHealth(String summaryJson, String validationJson, String status,
                                         Instant approvedAt, UUID approvedBy) {
        String snapshotStatus;
        List<BaselineHealthDto.IssueDto> issues = new ArrayList<>();

        if (validationJson == null || validationJson.isBlank()) {
            snapshotStatus = "DRAFT";
        } else {
            try {
                Map<String, Object> validation = objectMapper.readValue(validationJson, MAP_TYPE);
                Boolean valid = (Boolean) validation.get("valid");
                Object issuesObj = validation.get("issues");
                if (issuesObj == null) issuesObj = validation.get("errors");
                if (issuesObj instanceof List<?> issueList) {
                    for (Object item : issueList) {
                        if (item instanceof Map<?, ?> issueMap) {
                            issues.add(new BaselineHealthDto.IssueDto(
                                    toStr(issueMap.get("code")),
                                    toStr(issueMap.get("message")),
                                    toStr(issueMap.get("severity"))
                            ));
                        } else if (item instanceof String s) {
                            issues.add(new BaselineHealthDto.IssueDto(null, s, "ERROR"));
                        }
                    }
                }
                snapshotStatus = (Boolean.TRUE.equals(valid) && issues.isEmpty()) ? "VALID" : "INVALID";
            } catch (Exception e) {
                log.warn("BaselineSnapshotParser: failed to parse validationJson: {}", e.getMessage());
                snapshotStatus = "DRAFT";
            }
        }

        List<BaselineHealthDto.SourceCheckDto> sources = new ArrayList<>();
        if (summaryJson != null && !summaryJson.isBlank()) {
            try {
                Map<String, Object> summary = objectMapper.readValue(summaryJson, MAP_TYPE);
                addSourceCheck(sources, "schedule", summary.get("scheduleStatus"));
                addSourceCheck(sources, "estimation", summary.get("estimationStatus"));
                addSourceCheck(sources, "finance", summary.get("financeStatus"));
                addSourceCheck(sources, "quote", summary.get("quoteStatus"));
            } catch (Exception e) {
                log.warn("BaselineSnapshotParser: failed to parse summaryJson for health: {}", e.getMessage());
            }
        }

        BaselineHealthDto.ApprovalInfoDto approval = new BaselineHealthDto.ApprovalInfoDto(status, approvedAt, approvedBy);
        return new BaselineHealthDto(snapshotStatus, sources, approval, issues);
    }

    public BaselineProvenanceDto parseProvenance(String snapshotJson, Instant capturedAt) {
        if (snapshotJson == null || snapshotJson.isBlank()) {
            return new BaselineProvenanceDto(List.of());
        }
        String capturedAtStr = capturedAt != null ? capturedAt.toString() : null;
        List<BaselineProvenanceDto.EntryDto> entries = new ArrayList<>();
        try {
            Map<String, Object> snapshot = objectMapper.readValue(snapshotJson, MAP_TYPE);

            Object scheduleObj = snapshot.get("schedule");
            if (scheduleObj instanceof Map<?, ?> scheduleMap) {
                String schedStatus = toStr(scheduleMap.get("status"));
                if (!"MISSING".equals(schedStatus) && !"NOT_APPLICABLE".equals(schedStatus)) {
                    UUID runId = toUuid(scheduleMap.get("scheduleRunId"));
                    String runStatus = toStr(scheduleMap.get("runStatus"));
                    entries.add(new BaselineProvenanceDto.EntryDto("schedule", runId, "Schedule Run", runStatus, capturedAtStr));
                }
            }

            Object estimationObj = snapshot.get("estimation");
            if (estimationObj instanceof Map<?, ?> estimationMap) {
                String estStatus = toStr(estimationMap.get("status"));
                if (!"MISSING".equals(estStatus) && !"NOT_APPLICABLE".equals(estStatus)) {
                    UUID runId = toUuid(estimationMap.get("estimationRunId"));
                    entries.add(new BaselineProvenanceDto.EntryDto("estimation", runId, "Estimation Run", estStatus, capturedAtStr));
                }
            }

            Object financeObj = snapshot.get("finance");
            if (financeObj instanceof Map<?, ?> financeMap) {
                String finStatus = toStr(financeMap.get("status"));
                if (!"MISSING".equals(finStatus) && !"NOT_APPLICABLE".equals(finStatus)) {
                    UUID scenarioId = toUuid(financeMap.get("financeScenarioId"));
                    String scenarioStatus = toStr(financeMap.get("scenarioStatus"));
                    entries.add(new BaselineProvenanceDto.EntryDto("finance", scenarioId, "Finance Scenario", scenarioStatus, capturedAtStr));
                }
            }

            Object quoteObj = snapshot.get("quote");
            if (quoteObj instanceof Map<?, ?> quoteMap) {
                String qStatus = toStr(quoteMap.get("status"));
                if (!"MISSING".equals(qStatus) && !"NOT_APPLICABLE".equals(qStatus)) {
                    UUID versionId = toUuid(quoteMap.get("quoteVersionId"));
                    String versionStatus = toStr(quoteMap.get("versionStatus"));
                    entries.add(new BaselineProvenanceDto.EntryDto("quote", versionId, "Quote Version", versionStatus, capturedAtStr));
                }
            }
        } catch (Exception e) {
            log.warn("BaselineSnapshotParser: failed to parse provenance from snapshotJson: {}", e.getMessage());
        }
        return new BaselineProvenanceDto(entries);
    }

    // --- package-private helpers for BaselineCompareService ---

    List<Map<String, Object>> parseListOfMaps(String json, String key) {
        if (json == null || json.isBlank()) return List.of();
        try {
            Map<String, Object> root = objectMapper.readValue(json, MAP_TYPE);
            return toListOfMaps(root.get(key));
        } catch (Exception e) {
            log.warn("BaselineSnapshotParser: failed to parse list '{}' from json: {}", key, e.getMessage());
            return List.of();
        }
    }

    // --- private helpers ---

    private void addSourceCheck(List<BaselineHealthDto.SourceCheckDto> sources, String source, Object statusObj) {
        String s = toStr(statusObj);
        if (s != null) {
            sources.add(new BaselineHealthDto.SourceCheckDto(source, s));
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> toListOfMaps(Object obj) {
        if (!(obj instanceof List<?> list)) return List.of();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                result.add((Map<String, Object>) map);
            }
        }
        return result;
    }

    private int toInt(Object obj) {
        if (obj == null) return 0;
        try { return Integer.parseInt(obj.toString()); } catch (NumberFormatException e) { return 0; }
    }

    private String toStr(Object obj) {
        if (obj == null) return null;
        String s = obj.toString();
        return s.isBlank() ? null : s;
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return null;
        try { return new BigDecimal(obj.toString()); } catch (NumberFormatException e) { return null; }
    }

    private UUID toUuid(Object obj) {
        if (obj == null) return null;
        try { return UUID.fromString(obj.toString()); } catch (IllegalArgumentException e) { return null; }
    }
}
