package com.company.scopery.modules.iam.resource.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateIamAuthResourceAction {

    private final IamAuthResourceRepository iamAuthResourceRepository;
    private final IamActivityLogger activityLogger;

    public CreateIamAuthResourceAction(IamAuthResourceRepository iamAuthResourceRepository,
                                       IamActivityLogger activityLogger) {
        this.iamAuthResourceRepository = iamAuthResourceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamAuthResourceResponse execute(CreateIamAuthResourceCommand command) {
        IamResourceCode code = IamResourceCode.of(command.code());
        IamResourceType resourceType = IamEnumParser.parseRequired(
                IamResourceType.class, command.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");

        if (resourceType != IamResourceType.GLOBAL) {
            throw IamExceptions.iamAuthResourceManualCreateRequiresGlobal(resourceType.name());
        }

        if (iamAuthResourceRepository.existsByCodeAndResourceType(code, resourceType)) {
            throw IamExceptions.iamAuthResourceCodeAlreadyExists(code.value(), resourceType.name());
        }

        IamAuthResource resource = IamAuthResource.create(code, resourceType, command.name(), command.description());
        IamAuthResource saved = iamAuthResourceRepository.save(resource);

        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.CREATE_IAM_AUTH_RESOURCE,
                "Auth resource created: " + saved.code().value());

        return IamAuthResourceResponse.from(saved);
    }
}
