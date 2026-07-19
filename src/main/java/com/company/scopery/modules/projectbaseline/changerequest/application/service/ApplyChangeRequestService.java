package com.company.scopery.modules.projectbaseline.changerequest.application.service;

import com.company.scopery.modules.project.shared.support.BaselineApplyContext;
import com.company.scopery.modules.project.task.application.action.CreateTaskAction;
import com.company.scopery.modules.project.task.application.action.UpdateTaskAction;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.project.task.application.command.UpdateTaskCommand;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.command.CreateTaskDependencyCommand;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemOperation;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemTargetType;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ApplyChangeRequestService {
    private final ChangeRequestItemRepository items;
    private final CreateTaskAction createTaskAction;
    private final UpdateTaskAction updateTaskAction;
    private final CreateTaskDependencyAction createTaskDependencyAction;
    private final TaskRepository tasks;
    private final ObjectMapper objectMapper;

    public ApplyChangeRequestService(ChangeRequestItemRepository items,
                                     CreateTaskAction createTaskAction,
                                     UpdateTaskAction updateTaskAction,
                                     CreateTaskDependencyAction createTaskDependencyAction,
                                     TaskRepository tasks,
                                     ObjectMapper objectMapper) {
        this.items = items; this.createTaskAction = createTaskAction; this.updateTaskAction = updateTaskAction;
        this.createTaskDependencyAction = createTaskDependencyAction; this.tasks = tasks; this.objectMapper = objectMapper;
    }

    public void applyAll(UUID projectId, UUID changeRequestId) {
        List<ChangeRequestItem> list = items.findByChangeRequestId(changeRequestId);
        for (ChangeRequestItem item : list) {
            validateSupported(item);
        }
        BaselineApplyContext.run(() -> {
            for (ChangeRequestItem item : list) {
                applyOne(projectId, item);
                items.save(item.markApplied());
            }
        });
    }

    private void validateSupported(ChangeRequestItem item) {
        boolean supported =
                (item.targetType() == ChangeItemTargetType.TASK && (item.operation() == ChangeItemOperation.CREATE || item.operation() == ChangeItemOperation.UPDATE))
                || (item.targetType() == ChangeItemTargetType.TASK_DEPENDENCY && item.operation() == ChangeItemOperation.CREATE);
        if (!supported) {
            throw ProjectBaselineExceptions.unsupportedOperation(item.targetType().name(), item.operation().name());
        }
    }

    private void applyOne(UUID projectId, ChangeRequestItem item) {
        try {
            JsonNode payload = objectMapper.readTree(item.applyPayloadJson() == null ? "{}" : item.applyPayloadJson());
            JsonNode body = payload.has("payload") ? payload.get("payload") : payload;
            if (item.targetType() == ChangeItemTargetType.TASK && item.operation() == ChangeItemOperation.CREATE) {
                createTaskAction.execute(new CreateTaskCommand(
                        projectId,
                        uuid(body, "projectPhaseId"),
                        uuid(body, "wbsNodeId"),
                        text(body, "code"),
                        text(body, "title"),
                        text(body, "description"),
                        uuid(body, "inChargeUserId"),
                        text(body, "plannedRoleCode"),
                        text(body, "plannedRoleName"),
                        decimal(body, "estimateHours"),
                        date(body, "plannedStartDate"),
                        date(body, "dueDate"),
                        text(body, "priority") == null ? "MEDIUM" : text(body, "priority")
                ));
            } else if (item.targetType() == ChangeItemTargetType.TASK && item.operation() == ChangeItemOperation.UPDATE) {
                if (item.targetId() == null) throw ProjectBaselineExceptions.invalidPayload("targetId required for TASK UPDATE");
                Task task = tasks.findById(item.targetId())
                        .orElseThrow(() -> ProjectBaselineExceptions.itemTargetMismatch(item.targetId(), projectId));
                if (!task.projectId().equals(projectId)) {
                    throw ProjectBaselineExceptions.itemTargetMismatch(item.targetId(), projectId);
                }
                updateTaskAction.execute(new UpdateTaskCommand(
                        task.id(), projectId,
                        body.hasNonNull("projectPhaseId") ? uuid(body, "projectPhaseId") : task.projectPhaseId(),
                        body.has("wbsNodeId") ? uuid(body, "wbsNodeId") : task.wbsNodeId(),
                        body.hasNonNull("title") ? text(body, "title") : task.title(),
                        body.has("description") ? text(body, "description") : task.description(),
                        body.has("inChargeUserId") ? uuid(body, "inChargeUserId") : task.inChargeUserId(),
                        body.has("plannedRoleCode") ? text(body, "plannedRoleCode") : task.plannedRoleCode(),
                        body.has("plannedRoleName") ? text(body, "plannedRoleName") : task.plannedRoleName(),
                        body.has("estimateHours") ? decimal(body, "estimateHours") : task.estimateHours(),
                        body.has("plannedStartDate") ? date(body, "plannedStartDate") : task.plannedStartDate(),
                        body.has("dueDate") ? date(body, "dueDate") : task.dueDate(),
                        body.hasNonNull("priority") ? text(body, "priority") : task.priority().name()
                ));
            } else if (item.targetType() == ChangeItemTargetType.TASK_DEPENDENCY && item.operation() == ChangeItemOperation.CREATE) {
                createTaskDependencyAction.execute(new CreateTaskDependencyCommand(
                        projectId,
                        uuid(body, "predecessorTaskId"),
                        uuid(body, "successorTaskId"),
                        text(body, "dependencyType") == null ? "FINISH_TO_START" : text(body, "dependencyType"),
                        body.has("lagDays") ? body.get("lagDays").asInt() : 0
                ));
            }
        } catch (com.company.scopery.common.exception.AppException e) {
            throw e;
        } catch (Exception e) {
            throw ProjectBaselineExceptions.applyFailed(e.getMessage());
        }
    }

    private static String text(JsonNode n, String f) {
        return n.hasNonNull(f) ? n.get(f).asText() : null;
    }
    private static UUID uuid(JsonNode n, String f) {
        return n.hasNonNull(f) ? UUID.fromString(n.get(f).asText()) : null;
    }
    private static BigDecimal decimal(JsonNode n, String f) {
        return n.hasNonNull(f) ? new BigDecimal(n.get(f).asText()) : null;
    }
    private static LocalDate date(JsonNode n, String f) {
        return n.hasNonNull(f) ? LocalDate.parse(n.get(f).asText()) : null;
    }
}
