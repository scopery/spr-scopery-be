package com.company.scopery.modules.reporting.activityfeed.application.service;

import com.company.scopery.common.audit.ActivityLogJpaEntity;
import com.company.scopery.common.audit.ActivityLogRepository;
import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.reporting.activityfeed.application.response.ActivityFeedItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectActivityFeedQueryService {

    private final ActivityLogRepository activityLogs;
    private final ProjectWorkspaceAuthorizationService projectAuthorization;

    public ProjectActivityFeedQueryService(ActivityLogRepository activityLogs,
                                           ProjectWorkspaceAuthorizationService projectAuthorization) {
        this.activityLogs = activityLogs;
        this.projectAuthorization = projectAuthorization;
    }

    @Transactional(readOnly = true)
    public PageResult<ActivityFeedItemResponse> list(UUID projectId, int page, int size) {
        projectAuthorization.requireProjectView(projectId);
        Page<ActivityLogJpaEntity> result = activityLogs.findProjectActivityFeed(
                projectId.toString(),
                ProjectEntityTypes.PROJECT,
                PageRequestUtils.of(page, size));
        return PageResult.fromSpringPage(result.map(this::toResponse));
    }

    private ActivityFeedItemResponse toResponse(ActivityLogJpaEntity entity) {
        return new ActivityFeedItemResponse(
                entity.getCreatedAt(),
                entity.getActorId(),
                entity.getActorName(),
                entity.getAction(),
                entity.getMessage());
    }
}
