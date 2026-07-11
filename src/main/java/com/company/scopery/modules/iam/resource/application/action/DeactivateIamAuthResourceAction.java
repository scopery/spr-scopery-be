package com.company.scopery.modules.iam.resource.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.platform.config.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import com.company.scopery.modules.iam.resource.application.command.DeactivateIamAuthResourceCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DeactivateIamAuthResourceAction {

    private final IamAuthResourceRepository iamAuthResourceRepository;
    private final IamActivityLogger activityLogger;

    public DeactivateIamAuthResourceAction(IamAuthResourceRepository iamAuthResourceRepository,
                                           IamActivityLogger activityLogger) {
        this.iamAuthResourceRepository = iamAuthResourceRepository;
        this.activityLogger = activityLogger;
    }

    @CacheEvict(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional
    public IamAuthResourceResponse execute(DeactivateIamAuthResourceCommand command) {
        IamAuthResource resource = iamAuthResourceRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(command.id()));
        IamAuthResource saved = iamAuthResourceRepository.save(resource.deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_AUTH_RESOURCE,
                "Auth resource deactivated: " + saved.code().value());
        return IamAuthResourceResponse.from(saved);
    }
}
