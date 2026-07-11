package com.company.scopery.modules.iam.access.domain.model;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationExplanation;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;

import java.util.UUID;

public interface AccessExplanationRepository {

    AuthorizationExplanation explain(
            UUID actorUserId,
            String permissionCode,
            String actionCode,
            IamResourceType resourceType,
            UUID resourceRefId);
}
