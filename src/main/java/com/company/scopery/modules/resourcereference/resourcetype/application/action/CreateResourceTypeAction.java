package com.company.scopery.modules.resourcereference.resourcetype.application.action;

import com.company.scopery.modules.resourcereference.resourcetype.application.command.CreateResourceTypeCommand;
import com.company.scopery.modules.resourcereference.resourcetype.application.response.ResourceTypeResponse;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeDefinition;
import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeRepository;
import com.company.scopery.modules.resourcereference.shared.activity.ResourceReferenceActivityLogger;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceActivityActions;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceEntityTypes;
import com.company.scopery.modules.resourcereference.shared.error.ResourceReferenceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateResourceTypeAction {

    private final MentionResourceTypeRepository repo;
    private final ResourceReferenceActivityLogger activityLogger;

    public CreateResourceTypeAction(MentionResourceTypeRepository repo,
                                     ResourceReferenceActivityLogger activityLogger) {
        this.repo = repo;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ResourceTypeResponse execute(CreateResourceTypeCommand c) {
        if (repo.existsByCode(c.code())) {
            throw ResourceReferenceExceptions.resourceTypeAlreadyExists(c.code());
        }
        MentionResourceTypeDefinition saved = repo.save(
                MentionResourceTypeDefinition.create(c.code(), c.displayName(), c.description(), c.isSystem()));
        activityLogger.logSuccess(ResourceReferenceEntityTypes.RESOURCE_TYPE, saved.id(),
                ResourceReferenceActivityActions.RESOURCE_TYPE_CREATED, "Resource type created: " + saved.code());
        return ResourceTypeResponse.from(saved);
    }
}
