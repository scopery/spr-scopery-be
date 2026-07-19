package com.company.scopery.modules.resourcereference.resourcetype.application.action;

import com.company.scopery.modules.resourcereference.resourcetype.application.command.DeprecateResourceTypeCommand;
import com.company.scopery.modules.resourcereference.resourcetype.application.response.ResourceTypeResponse;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeRepository;
import com.company.scopery.modules.resourcereference.shared.activity.ResourceReferenceActivityLogger;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceActivityActions;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceEntityTypes;
import com.company.scopery.modules.resourcereference.shared.error.ResourceReferenceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeprecateResourceTypeAction {

    private final MentionResourceTypeRepository repo;
    private final ResourceReferenceActivityLogger activityLogger;

    public DeprecateResourceTypeAction(MentionResourceTypeRepository repo,
                                        ResourceReferenceActivityLogger activityLogger) {
        this.repo = repo;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ResourceTypeResponse execute(DeprecateResourceTypeCommand c) {
        var type = repo.findById(c.id())
                .orElseThrow(() -> ResourceReferenceExceptions.resourceTypeNotFound(c.id().toString()));
        var updated = repo.save(type.disable());
        activityLogger.logSuccess(ResourceReferenceEntityTypes.RESOURCE_TYPE, updated.id(),
                ResourceReferenceActivityActions.RESOURCE_TYPE_DEPRECATED, "Resource type deprecated: " + updated.code());
        return ResourceTypeResponse.from(updated);
    }
}
