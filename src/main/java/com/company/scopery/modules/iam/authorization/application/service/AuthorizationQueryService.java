package com.company.scopery.modules.iam.authorization.application.service;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.authorization.application.query.CheckAccessByRightQuery;
import com.company.scopery.modules.iam.authorization.application.query.CheckAuthorizationQuery;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationDecisionResponse;
import com.company.scopery.modules.iam.authorization.application.response.AuthorizationExplanationResponse;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationReadRepository;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthorizationQueryService {

    private final AuthorizationDecisionService decisionService;
    private final CurrentUserAuthorizationService currentUserService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final AuthorizationReadRepository readRepository;

    public AuthorizationQueryService(AuthorizationDecisionService decisionService,
                                     CurrentUserAuthorizationService currentUserService,
                                     IamSystemAuthorizationService systemAuthorizationService,
                                     AuthorizationReadRepository readRepository) {
        this.decisionService = decisionService;
        this.currentUserService = currentUserService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public AuthorizationExplanationResponse check(CheckAuthorizationQuery query) {
        IamResourceType resourceType = IamEnumParser.parseRequired(IamResourceType.class, query.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");

        IamAuthResource resource;
        UUID resourceRefId;
        if (resourceType == IamResourceType.GLOBAL) {
            // GLOBAL is a singleton — no business refId; look up by code
            resource = readRepository.findGlobalSystemResource()
                    .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound((UUID) null));
            resourceRefId = resource.refId();
        } else {
            resourceRefId = parseResourceRefId(query.resourceRefId());
            resource = readRepository.findResourceByRefIdAndType(resourceRefId, resourceType)
                    .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(resourceRefId));
        }

        UUID actorId = currentUserService.resolveCurrentUser().id();
        var explanation = decisionService.explainAccess(new AuthorizationRequest(
                actorId, resource.id(), null,
                query.permissionCode().trim().toUpperCase(), query.actionCode().trim().toUpperCase()));
        return AuthorizationExplanationResponse.from(query.permissionCode(), query.actionCode(), resourceType.name(),
                resourceRefId, explanation);
    }

    @Transactional(readOnly = true)
    public AuthorizationDecisionResponse checkByRight(CheckAccessByRightQuery query) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        if (!actorId.equals(query.userId())) {
            systemAuthorizationService.requireSystemRight(
                    IamAuthorities.SYSTEM_IAM_VIEW_ACCESS_GRANT.legacyRightCode());
        }
        AuthorizationDecision decision = decisionService.canAccess(
                new AuthorizationRequest(query.userId(), query.resourceId(), query.rightCode()));
        return AuthorizationDecisionResponse.from(
                query.userId(), query.resourceId(), query.rightCode(), decision);
    }

    private UUID parseResourceRefId(String resourceRefIdValue) {
        try {
            return UUID.fromString(resourceRefIdValue);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("resourceRefId must be a valid UUID");
        }
    }
}
