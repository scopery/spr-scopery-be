package com.company.scopery.modules.project.task.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.application.query.SearchTaskQuery;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskQueryService {

    private final TaskRepository taskRepository;
    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public TaskQueryService(TaskRepository taskRepository,
                            WbsNodeRepository wbsNodeRepository,
                            ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.wbsNodeRepository = wbsNodeRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID projectId, UUID id) {
        authorizationService.requireTaskView(projectId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.taskNotFound(id));

        if (!task.projectId().equals(projectId)) {
            throw ProjectExceptions.taskProjectMismatch(id, projectId);
        }

        return TaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public PageResult<TaskResponse> searchTasks(SearchTaskQuery query) {
        authorizationService.requireTaskView(query.projectId());

        TaskStatus status = ProjectEnumParser.parseOptional(
                TaskStatus.class, query.status(), "TASK_INVALID_STATUS", "status");
        TaskPriority priority = ProjectEnumParser.parseOptional(
                TaskPriority.class, query.priority(), "TASK_INVALID_PRIORITY", "priority");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), ProjectSortFields.CREATED_AT, false);
        return taskRepository.search(
                query.projectId(), query.projectPhaseId(), query.wbsNodeId(),
                status, priority, query.keyword(), pageQuery)
                .map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listTasksByWbsNode(UUID wbsNodeId) {
        var wbsNode = wbsNodeRepository.findById(wbsNodeId)
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(wbsNodeId));
        authorizationService.requireTaskView(wbsNode.projectId());

        return taskRepository.findAllByWbsNodeId(wbsNodeId)
                .stream().map(TaskResponse::from).toList();
    }
}
