package com.company.scopery.modules.projectbaseline.changerequestitem.application.service;

import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.response.ChangeRequestItemResponse;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemTargetType;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChangeRequestItemQueryService {
    private final ChangeRequestItemRepository items;
    private final TaskRepository tasks;
    private final FunctionalItemRepository functionalItems;
    private final ProjectBaselineAuthorizationService authorization;

    public ChangeRequestItemQueryService(ChangeRequestItemRepository items,
                                         TaskRepository tasks,
                                         FunctionalItemRepository functionalItems,
                                         ProjectBaselineAuthorizationService authorization) {
        this.items = items; this.tasks = tasks;
        this.functionalItems = functionalItems; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ChangeRequestItemResponse> list(UUID projectId, UUID changeRequestId) {
        authorization.requireItemView(projectId);
        return items.findByChangeRequestId(changeRequestId).stream()
                .map(i -> enriched(i, projectId))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChangeRequestItemResponse get(UUID projectId, UUID changeRequestId, UUID itemId) {
        authorization.requireItemView(projectId);
        var item = items.findByIdAndChangeRequestId(itemId, changeRequestId)
                .orElseThrow(() -> ProjectBaselineExceptions.itemNotFound(itemId));
        return enriched(item, projectId);
    }

    private ChangeRequestItemResponse enriched(ChangeRequestItem item, UUID projectId) {
        if (item.targetId() == null) return ChangeRequestItemResponse.from(item);
        if (item.targetType() == ChangeItemTargetType.TASK) {
            return tasks.findById(item.targetId())
                    .map(t -> ChangeRequestItemResponse.from(item, t.code(), t.title(), null))
                    .orElseGet(() -> ChangeRequestItemResponse.from(item));
        }
        if (item.targetType() == ChangeItemTargetType.FUNCTION) {
            return functionalItems.findByIdAndProjectId(item.targetId(), projectId)
                    .map(f -> ChangeRequestItemResponse.from(item, f.code(), f.title(), null))
                    .orElseGet(() -> ChangeRequestItemResponse.from(item));
        }
        return ChangeRequestItemResponse.from(item);
    }
}
