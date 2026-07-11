package com.company.scopery.modules.project.taskdependency.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectSortFields;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.taskdependency.application.query.SearchTaskDependencyQuery;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TaskDependencyQueryService {

    private final TaskDependencyRepository taskDependencyRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public TaskDependencyQueryService(TaskDependencyRepository taskDependencyRepository,
                                      ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskDependencyRepository = taskDependencyRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public TaskDependencyResponse getTaskDependency(UUID id) {
        TaskDependency dep = taskDependencyRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.taskDependencyNotFound(id));
        authorizationService.requireProjectPermission(dep.projectId(), IamAuthorities.PROJECT_TASK_VIEW);
        return TaskDependencyResponse.from(dep);
    }

    @Transactional(readOnly = true)
    public PageResult<TaskDependencyResponse> searchTaskDependencies(SearchTaskDependencyQuery query) {
        authorizationService.requireProjectPermission(query.projectId(), IamAuthorities.PROJECT_TASK_VIEW);

        TaskDependencyStatus status = ProjectEnumParser.parseOptional(
                TaskDependencyStatus.class, query.status(),
                "TASK_DEP_INVALID_STATUS", "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), ProjectSortFields.CREATED_AT, false);
        return taskDependencyRepository.search(query.projectId(), query.taskId(), status, pageQuery)
                .map(TaskDependencyResponse::from);
    }
}
