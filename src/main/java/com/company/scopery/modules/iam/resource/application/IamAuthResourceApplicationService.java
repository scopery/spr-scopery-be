package com.company.scopery.modules.iam.resource.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.UpdateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.query.SearchIamAuthResourceQuery;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.platform.config.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamAuthResourceApplicationService {

    private final IamAuthResourceRepository iamAuthResourceRepository;
    private final IamActivityLogger activityLogger;

    public IamAuthResourceApplicationService(IamAuthResourceRepository iamAuthResourceRepository,
                                              IamActivityLogger activityLogger) {
        this.iamAuthResourceRepository = iamAuthResourceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamAuthResourceResponse createResource(CreateIamAuthResourceCommand command) {
        IamResourceCode code = IamResourceCode.of(command.code());
        IamResourceType resourceType = IamEnumParser.parseRequired(
                IamResourceType.class, command.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");

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

    @CacheEvict(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#command.id()")
    @Transactional
    public IamAuthResourceResponse updateResource(UpdateIamAuthResourceCommand command) {
        IamAuthResource resource = findOrThrow(command.id());
        IamAuthResource saved = iamAuthResourceRepository.save(
                resource.update(command.name(), command.description()));

        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.UPDATE_IAM_AUTH_RESOURCE,
                "Auth resource updated: " + saved.code().value());

        return IamAuthResourceResponse.from(saved);
    }

    @CacheEvict(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional
    public IamAuthResourceResponse activateResource(UUID id) {
        IamAuthResource saved = iamAuthResourceRepository.save(findOrThrow(id).activate());
        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.ACTIVATE_IAM_AUTH_RESOURCE,
                "Auth resource activated: " + saved.code().value());
        return IamAuthResourceResponse.from(saved);
    }

    @CacheEvict(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional
    public IamAuthResourceResponse deactivateResource(UUID id) {
        IamAuthResource saved = iamAuthResourceRepository.save(findOrThrow(id).deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_AUTH_RESOURCE, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_AUTH_RESOURCE,
                "Auth resource deactivated: " + saved.code().value());
        return IamAuthResourceResponse.from(saved);
    }

    @Cacheable(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional(readOnly = true)
    public IamAuthResourceResponse getResource(UUID id) {
        return IamAuthResourceResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<IamAuthResourceResponse> searchResources(SearchIamAuthResourceQuery query) {
        IamResourceType resourceType = IamEnumParser.parseOptional(
                IamResourceType.class, query.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamResourceStatus status = IamEnumParser.parseOptional(
                IamResourceStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, IamSortFields.CREATED_AT));
        return iamAuthResourceRepository.findAll(query.keyword(), resourceType, status, pageable)
                .map(IamAuthResourceResponse::from);
    }

    private IamAuthResource findOrThrow(UUID id) {
        return iamAuthResourceRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(id));
    }
}
