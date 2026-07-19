package com.company.scopery.modules.iam.ownerpolicy.application.listeners;

import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamInheritanceScope;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Idempotent ensure of ORGANIZATION / WORKSPACE / TEAM owner policies (complements V36 seed).
 */
@Component
@Order(30)
public class IamOwnerPolicyCatalogInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamOwnerPolicyCatalogInitializer.class);

    private final IamOwnerPolicyRepository ownerPolicyRepository;

    public IamOwnerPolicyCatalogInitializer(IamOwnerPolicyRepository ownerPolicyRepository) {
        this.ownerPolicyRepository = ownerPolicyRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ensure(IamResourceType.ORGANIZATION, IamInheritanceScope.DESCENDANTS, 2, ORGANIZATION_ACTIONS);
        ensure(IamResourceType.WORKSPACE, IamInheritanceScope.DESCENDANTS, 1, WORKSPACE_ACTIONS);
        ensure(IamResourceType.TEAM, IamInheritanceScope.SELF_ONLY, 1, TEAM_ACTIONS);
        log.info("[IamOwnerPolicyCatalog] Owner policy catalog ensure complete");
    }

    private void ensure(IamResourceType resourceType, IamInheritanceScope scope,
                        int delegationDepth, List<IamOwnerPolicyAction> actions) {
        if (ownerPolicyRepository.findActiveByResourceType(resourceType).isPresent()) {
            return;
        }
        IamOwnerPolicy created = IamOwnerPolicy.create(resourceType, 1, actions, scope, true, delegationDepth);
        ownerPolicyRepository.save(created);
        log.info("[IamOwnerPolicyCatalog] Seeded missing owner policy for {}", resourceType);
    }

    private static IamOwnerPolicyAction a(String permissionCode, String actionCode) {
        return new IamOwnerPolicyAction(permissionCode, actionCode);
    }

    private static final List<IamOwnerPolicyAction> ORGANIZATION_ACTIONS = List.of(
            a("ORGANIZATION_MANAGEMENT", "VIEW"),
            a("ORGANIZATION_MANAGEMENT", "MANAGE"),
            a("ORGANIZATION_MANAGEMENT", "CREATE_WORKSPACE"),
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "CREATE"),
            a("TEAM_MANAGEMENT", "UPDATE"),
            a("TEAM_MANAGEMENT", "ARCHIVE"),
            a("TEAM_MANAGEMENT", "MANAGE")
    );

    private static final List<IamOwnerPolicyAction> WORKSPACE_ACTIONS = List.of(
            a("WORKSPACE_MANAGEMENT", "VIEW"),
            a("WORKSPACE_MANAGEMENT", "MANAGE"),
            a("WORKSPACE_MANAGEMENT", "MANAGE_SETTING"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_MEMBER"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_ACCESS"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_PERMISSION"),
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "CREATE"),
            a("TEAM_MANAGEMENT", "UPDATE"),
            a("TEAM_MANAGEMENT", "ARCHIVE"),
            a("TEAM_MANAGEMENT", "MANAGE")
    );

    private static final List<IamOwnerPolicyAction> TEAM_ACTIONS = List.of(
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "MANAGE"),
            a("TEAM_MEMBER_MANAGEMENT", "VIEW"),
            a("TEAM_MEMBER_MANAGEMENT", "ADD"),
            a("TEAM_MEMBER_MANAGEMENT", "REMOVE")
    );
}
