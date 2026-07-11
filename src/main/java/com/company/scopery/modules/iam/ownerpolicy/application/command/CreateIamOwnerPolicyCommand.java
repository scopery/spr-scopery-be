package com.company.scopery.modules.iam.ownerpolicy.application.command;

import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;

import java.util.List;

public record CreateIamOwnerPolicyCommand(
        String resourceType,
        String inheritanceScope,
        boolean canDelegate,
        int delegationDepth,
        List<IamOwnerPolicyAction> actions) {
}
