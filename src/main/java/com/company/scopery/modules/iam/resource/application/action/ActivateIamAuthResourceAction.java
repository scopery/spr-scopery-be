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
import com.company.scopery.modules.iam.resource.application.command.ActivateIamAuthResourceCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ActivateIamAuthResourceAction {

    private final IamAuthResourceRepository iamAuthResourceRepository;
    private final IamActivityLogger activityLogger;

    public ActivateIamAuthResourceAction(IamAuthResourceRepository iamAuthResourceRepository,
                                         IamActivityLogger activityLogger) {
        this.iamAuthResourceRepository = iamAuthResourceRepository;
        this.activityLogger = activityLogger;
    }

    @CacheEvict(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional
    public IamAuthResourceResponse execute(ActivateIamAuthResourceCommand command) {
        IamAuthResource resource = iamAuthResourceRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(command.id()));
        IamAuthResource saved = iamAuthResourceRepository.save(resource.activate());
        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.ACTIVATE_IAM_AUTH_RESOURCE,
                "Auth resource activated: " + saved.code().value());
        return IamAuthResourceResponse.from(saved);
    }
}
