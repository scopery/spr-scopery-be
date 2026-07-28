package com.company.scopery.modules.productivity.workinbox.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.productivity.shared.authorization.ProductivityAuthorizationService;
import com.company.scopery.modules.productivity.workinbox.application.response.WorkInboxItemResponse;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkInboxQueryService {
    private final WorkInboxItemRepository repo;
    private final ProductivityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public WorkInboxQueryService(WorkInboxItemRepository repo,
                                  ProductivityAuthorizationService authorization,
                                  CurrentUserAuthorizationService currentUser) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    /**
     * Personal Work Inbox: all active items for the current user (userId security boundary).
     * Route workspace is only used for WORK_INBOX_VIEW authorization.
     */
    @Transactional(readOnly = true)
    public List<WorkInboxItemResponse> list(UUID workspaceId) {
        authorization.requireInboxView(workspaceId);
        UUID userId = currentUser.resolveCurrentUser().id();
        return repo.findActiveByUser(userId).stream().map(WorkInboxItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> counts(UUID workspaceId) {
        List<WorkInboxItemResponse> items = list(workspaceId);
        long active = items.stream().filter(i -> "ACTIVE".equals(i.status())).count();
        return Map.of("active", active, "total", (long) items.size());
    }
}
