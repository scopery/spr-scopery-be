package com.company.scopery.modules.iam.access.application.service;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.access.application.query.ExplainAccessQuery;
import com.company.scopery.modules.iam.access.domain.model.AccessExplanationRepository;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationExplanationResponse;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamAccessQueryService {

    private final AccessExplanationRepository accessExplanationRepository;
    private final CurrentUserAuthorizationService currentUserService;

    public IamAccessQueryService(AccessExplanationRepository accessExplanationRepository,
                                 CurrentUserAuthorizationService currentUserService) {
        this.accessExplanationRepository = accessExplanationRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public AuthorizationExplanationResponse explainAccess(ExplainAccessQuery query) {
        IamResourceType resourceType = IamEnumParser.parseRequired(IamResourceType.class, query.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        UUID resourceRefId = parseResourceRefId(query.resourceRefId());
        UUID actorId = currentUserService.resolveCurrentUser().id();
        var explanation = accessExplanationRepository.explain(
                actorId,
                query.permissionCode(),
                query.actionCode(),
                resourceType,
                resourceRefId);
        return AuthorizationExplanationResponse.from(
                query.permissionCode(), query.actionCode(), resourceType.name(), resourceRefId, explanation);
    }

    private UUID parseResourceRefId(String resourceRefIdValue) {
        try {
            return UUID.fromString(resourceRefIdValue);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("resourceRefId must be a valid UUID");
        }
    }
}
