package com.company.scopery.modules.iam.access.infrastructure.adapter;

import com.company.scopery.modules.iam.access.domain.model.AccessExplanationRepository;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationExplanation;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationReadRepository;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class JpaAccessExplanationRepository implements AccessExplanationRepository {

    private final AuthorizationReadRepository readRepository;
    private final AuthorizationDecisionService decisionService;

    public JpaAccessExplanationRepository(AuthorizationReadRepository readRepository,
                                          AuthorizationDecisionService decisionService) {
        this.readRepository = readRepository;
        this.decisionService = decisionService;
    }

    @Override
    public AuthorizationExplanation explain(
            UUID actorUserId,
            String permissionCode,
            String actionCode,
            IamResourceType resourceType,
            UUID resourceRefId) {
        IamAuthResource resource = readRepository.findResourceByRefIdAndType(resourceRefId, resourceType)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(resourceRefId));
        return decisionService.explainAccess(new AuthorizationRequest(
                actorUserId, resource.id(), null,
                permissionCode.trim().toUpperCase(), actionCode.trim().toUpperCase()));
    }
}
