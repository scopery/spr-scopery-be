package com.company.scopery.modules.iam.resource.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.resource.application.query.SearchIamAuthResourceQuery;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.platform.config.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamAuthResourceQueryService {

    private final IamAuthResourceRepository iamAuthResourceRepository;

    public IamAuthResourceQueryService(IamAuthResourceRepository iamAuthResourceRepository) {
        this.iamAuthResourceRepository = iamAuthResourceRepository;
    }

    @Cacheable(value = CacheConfig.IAM_AUTH_RESOURCES, key = "#id")
    @Transactional(readOnly = true)
    public IamAuthResourceResponse getResource(UUID id) {
        IamAuthResource resource = iamAuthResourceRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(id));
        return IamAuthResourceResponse.from(resource);
    }

    @Transactional(readOnly = true)
    public IamAuthResourceResponse getResourceByRef(String resourceTypeValue, UUID refId) {
        IamResourceType resourceType = IamEnumParser.parseRequired(
                IamResourceType.class, resourceTypeValue,
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamAuthResource resource = iamAuthResourceRepository.findByRefIdAndResourceType(refId, resourceType)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(refId));
        return IamAuthResourceResponse.from(resource);
    }

    @Transactional(readOnly = true)
    public IamAuthResourceResponse getResourceByCode(String resourceTypeValue, String codeValue) {
        IamResourceType resourceType = IamEnumParser.parseRequired(
                IamResourceType.class, resourceTypeValue,
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamResourceCode code = IamResourceCode.of(codeValue);
        IamAuthResource resource = iamAuthResourceRepository.findByCodeAndResourceType(code, resourceType)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(code.value()));
        return IamAuthResourceResponse.from(resource);
    }

    @Transactional(readOnly = true)
    public PageResult<IamAuthResourceResponse> searchResources(SearchIamAuthResourceQuery query) {
        IamResourceType resourceType = IamEnumParser.parseOptional(
                IamResourceType.class, query.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamResourceStatus status = IamEnumParser.parseOptional(
                IamResourceStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CREATED_AT, false);
        return iamAuthResourceRepository.findAll(query.keyword(), resourceType, status, pageQuery)
                .map(IamAuthResourceResponse::from);
    }
}
