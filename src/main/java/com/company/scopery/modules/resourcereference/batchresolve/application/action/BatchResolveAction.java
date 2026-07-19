package com.company.scopery.modules.resourcereference.batchresolve.application.action;

import com.company.scopery.modules.resourcereference.batchresolve.application.command.BatchResolveCommand;
import com.company.scopery.modules.resourcereference.batchresolve.application.response.ResolvedResourceResponse;
import com.company.scopery.modules.resourcereference.shared.activity.ResourceReferenceActivityLogger;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceActivityActions;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceEntityTypes;
import com.company.scopery.modules.resourcereference.shared.error.ResourceReferenceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BatchResolveAction {

    private static final int MAX_REFS = 200;

    private final ResourceReferenceActivityLogger activityLogger;

    public BatchResolveAction(ResourceReferenceActivityLogger activityLogger) {
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public List<ResolvedResourceResponse> execute(BatchResolveCommand c) {
        if (c.refs().size() > MAX_REFS) {
            throw ResourceReferenceExceptions.batchResolveLimitExceeded(c.refs().size(), MAX_REFS);
        }

        // Deduplicate while preserving order
        List<ResolvedResourceResponse> results = c.refs().stream()
                .distinct()
                .map(ref -> resolveRef(ref.resourceType(), ref.resourceId()))
                .toList();

        activityLogger.logSuccess(ResourceReferenceEntityTypes.RESOURCE_TYPE, null,
                ResourceReferenceActivityActions.BATCH_RESOLVE_EXECUTED,
                "Batch resolve: " + c.refs().size() + " refs");

        return results;
    }

    private ResolvedResourceResponse resolveRef(String resourceType, java.util.UUID resourceId) {
        // Each module would register a resolver — for now return a stub that marks as RESOLVED
        // Real implementation would delegate to module-specific query services
        return ResolvedResourceResponse.resolved(resourceType, resourceId, resourceType + ":" + resourceId);
    }
}
