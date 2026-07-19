package com.company.scopery.modules.project.gantt.application.service;

import com.company.scopery.modules.project.gantt.application.response.GanttCriticalPathResponse;
import com.company.scopery.modules.project.gantt.application.response.GanttCriticalPathTaskResponse;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Topological forward/backward CPM over a task dependency graph.
 */
public final class CriticalPathCalculator {

    private CriticalPathCalculator() {}

    public record TaskNode(UUID taskId, String title, LocalDate start, LocalDate finish, int durationDays) {}

    public record DependencyEdge(UUID predecessorTaskId, UUID successorTaskId, int lagDays, TaskDependencyType type) {
        public DependencyEdge(UUID predecessorTaskId, UUID successorTaskId, int lagDays) {
            this(predecessorTaskId, successorTaskId, lagDays, TaskDependencyType.FINISH_TO_START);
        }

        boolean ignoredForCpm() {
            return type != TaskDependencyType.FINISH_TO_START;
        }
    }

    public static GanttCriticalPathResponse compute(
            UUID projectId,
            UUID scheduleRunId,
            List<TaskNode> tasks,
            List<DependencyEdge> edges) {
        Map<UUID, TaskNode> nodes = new LinkedHashMap<>();
        for (TaskNode task : tasks) {
            if (task.start() != null && task.finish() != null && task.durationDays() > 0) {
                nodes.put(task.taskId(), task);
            }
        }
        if (nodes.isEmpty()) {
            return new GanttCriticalPathResponse(projectId, scheduleRunId, List.of(), List.of());
        }

        Map<UUID, List<DependencyEdge>> outgoing = new HashMap<>();
        Map<UUID, List<DependencyEdge>> incoming = new HashMap<>();
        for (DependencyEdge edge : edges) {
            if (edge.ignoredForCpm() || !nodes.containsKey(edge.predecessorTaskId())
                    || !nodes.containsKey(edge.successorTaskId())) {
                continue;
            }
            outgoing.computeIfAbsent(edge.predecessorTaskId(), k -> new ArrayList<>()).add(edge);
            incoming.computeIfAbsent(edge.successorTaskId(), k -> new ArrayList<>()).add(edge);
        }

        List<UUID> order = topologicalSort(nodes.keySet(), outgoing, incoming);
        if (order.isEmpty()) {
            return new GanttCriticalPathResponse(projectId, scheduleRunId, List.of(), List.of());
        }

        LocalDate projectStart = nodes.values().stream()
                .map(TaskNode::start)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        Map<UUID, LocalDate> es = new HashMap<>();
        Map<UUID, LocalDate> ef = new HashMap<>();
        for (UUID taskId : order) {
            TaskNode node = nodes.get(taskId);
            LocalDate earliestStart = projectStart;
            for (DependencyEdge edge : incoming.getOrDefault(taskId, List.of())) {
                LocalDate predFinish = ef.get(edge.predecessorTaskId());
                if (predFinish != null) {
                    LocalDate candidate = predFinish.plusDays(Math.max(0, edge.lagDays()) + 1L);
                    if (candidate.isAfter(earliestStart)) {
                        earliestStart = candidate;
                    }
                }
            }
            es.put(taskId, earliestStart);
            ef.put(taskId, earliestStart.plusDays(node.durationDays() - 1L));
        }

        LocalDate projectFinish = ef.values().stream().max(LocalDate::compareTo).orElse(projectStart);
        Map<UUID, LocalDate> lf = new HashMap<>();
        Map<UUID, LocalDate> ls = new HashMap<>();
        List<UUID> reverse = new ArrayList<>(order);
        Collections.reverse(reverse);
        for (UUID taskId : reverse) {
            TaskNode node = nodes.get(taskId);
            LocalDate latestFinish = projectFinish;
            for (DependencyEdge edge : outgoing.getOrDefault(taskId, List.of())) {
                LocalDate succStart = ls.get(edge.successorTaskId());
                if (succStart != null) {
                    LocalDate candidate = succStart.minusDays(Math.max(0, edge.lagDays()) + 1L);
                    if (candidate.isBefore(latestFinish)) {
                        latestFinish = candidate;
                    }
                }
            }
            lf.put(taskId, latestFinish);
            ls.put(taskId, latestFinish.minusDays(node.durationDays() - 1L));
        }

        List<GanttCriticalPathTaskResponse> results = new ArrayList<>();
        List<UUID> criticalIds = new ArrayList<>();
        for (UUID taskId : order) {
            TaskNode node = nodes.get(taskId);
            long slack = ChronoUnit.DAYS.between(es.get(taskId), ls.get(taskId));
            boolean critical = slack == 0;
            if (critical) {
                criticalIds.add(taskId);
            }
            results.add(new GanttCriticalPathTaskResponse(
                    taskId,
                    node.title(),
                    node.start(),
                    node.finish(),
                    node.durationDays(),
                    es.get(taskId),
                    ef.get(taskId),
                    ls.get(taskId),
                    lf.get(taskId),
                    slack,
                    critical));
        }
        return new GanttCriticalPathResponse(projectId, scheduleRunId, results, criticalIds);
    }

    public static int durationDays(LocalDate start, LocalDate finish) {
        if (start == null || finish == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(start, finish) + 1;
        return (int) Math.max(1, days);
    }

    private static List<UUID> topologicalSort(
            Set<UUID> taskIds,
            Map<UUID, List<DependencyEdge>> outgoing,
            Map<UUID, List<DependencyEdge>> incoming) {
        Map<UUID, Integer> indegree = new HashMap<>();
        for (UUID id : taskIds) {
            indegree.put(id, incoming.getOrDefault(id, List.of()).size());
        }
        Deque<UUID> queue = new ArrayDeque<>();
        for (UUID id : taskIds) {
            if (indegree.get(id) == 0) {
                queue.add(id);
            }
        }
        List<UUID> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            UUID current = queue.removeFirst();
            order.add(current);
            for (DependencyEdge edge : outgoing.getOrDefault(current, List.of())) {
                UUID succ = edge.successorTaskId();
                if (!taskIds.contains(succ)) {
                    continue;
                }
                int next = indegree.merge(succ, -1, Integer::sum);
                if (next == 0) {
                    queue.add(succ);
                }
            }
        }
        return order.size() == taskIds.size() ? order : List.of();
    }
}
