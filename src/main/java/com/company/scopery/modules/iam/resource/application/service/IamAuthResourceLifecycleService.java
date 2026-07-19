package com.company.scopery.modules.iam.resource.application.service;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Reusable AuthResource lifecycle sync for later module phases (org/workspace/team/project).
 */
@Service
public class IamAuthResourceLifecycleService {

    private final IamAuthResourceRepository resourceRepository;
    private final IamActivityLogger activityLogger;

    public IamAuthResourceLifecycleService(IamAuthResourceRepository resourceRepository,
                                           IamActivityLogger activityLogger) {
        this.resourceRepository = resourceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void syncDisplayName(UUID refId, IamResourceType resourceType, String name, String description) {
        resourceRepository.findByRefIdAndResourceType(refId, resourceType).ifPresent(resource -> {
            IamAuthResource updated = resourceRepository.save(resource.update(name, description));
            activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, updated.id(),
                    IamActivityActions.UPDATE_IAM_AUTH_RESOURCE,
                    "Auth resource synced: " + updated.code().value());
        });
    }

    @Transactional
    public void deactivateByRef(UUID refId, IamResourceType resourceType) {
        resourceRepository.findByRefIdAndResourceType(refId, resourceType).ifPresent(resource -> {
            if (resource.status() == IamResourceStatus.INACTIVE) {
                return;
            }
            IamAuthResource saved = resourceRepository.save(resource.deactivate());
            activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                    IamActivityActions.DEACTIVATE_IAM_AUTH_RESOURCE,
                    "Auth resource deactivated by lifecycle sync: " + saved.code().value());
        });
    }

    @Transactional
    public void activateByRef(UUID refId, IamResourceType resourceType) {
        resourceRepository.findByRefIdAndResourceType(refId, resourceType).ifPresent(resource -> {
            IamAuthResource saved = resourceRepository.save(resource.activate());
            activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                    IamActivityActions.ACTIVATE_IAM_AUTH_RESOURCE,
                    "Auth resource activated by lifecycle sync: " + saved.code().value());
        });
    }
}
