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
 * Bump targetVersion when WORKSPACE_ACTIONS or other action lists change — the initializer
 * will supersede the old policy and insert a new one automatically on next startup.
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
        ensure(IamResourceType.ORGANIZATION, IamInheritanceScope.DESCENDANTS, 2, 1, ORGANIZATION_ACTIONS);
        ensure(IamResourceType.WORKSPACE,    IamInheritanceScope.DESCENDANTS, 1, 2, WORKSPACE_ACTIONS);
        ensure(IamResourceType.TEAM,         IamInheritanceScope.SELF_ONLY,   1, 1, TEAM_ACTIONS);
        log.info("[IamOwnerPolicyCatalog] Owner policy catalog ensure complete");
    }

    private void ensure(IamResourceType resourceType, IamInheritanceScope scope,
                        int delegationDepth, int targetVersion, List<IamOwnerPolicyAction> actions) {
        var existing = ownerPolicyRepository.findActiveByResourceType(resourceType);
        if (existing.isPresent()) {
            if (existing.get().policyVersion() >= targetVersion) {
                return;
            }
            ownerPolicyRepository.save(existing.get().supersede());
            log.info("[IamOwnerPolicyCatalog] Superseded v{} owner policy for {}", existing.get().policyVersion(), resourceType);
        }
        IamOwnerPolicy created = IamOwnerPolicy.create(resourceType, targetVersion, actions, scope, true, delegationDepth);
        ownerPolicyRepository.save(created);
        log.info("[IamOwnerPolicyCatalog] Seeded v{} owner policy for {}", targetVersion, resourceType);
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
            // Workspace management
            a("WORKSPACE_MANAGEMENT", "VIEW"),
            a("WORKSPACE_MANAGEMENT", "UPDATE"),
            a("WORKSPACE_MANAGEMENT", "ARCHIVE"),
            a("WORKSPACE_MANAGEMENT", "MANAGE"),
            a("WORKSPACE_MANAGEMENT", "MANAGE_SETTING"),
            // Workspace access management
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_MEMBER"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_ACCESS"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_PERMISSION"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "INVITE_MEMBER"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_INVITATION"),
            a("WORKSPACE_ACCESS_MANAGEMENT", "MANAGE_JOIN_REQUEST"),
            // Workspace role management
            a("WORKSPACE_ROLE_MANAGEMENT", "VIEW"),
            a("WORKSPACE_ROLE_MANAGEMENT", "CREATE"),
            a("WORKSPACE_ROLE_MANAGEMENT", "UPDATE"),
            a("WORKSPACE_ROLE_MANAGEMENT", "DELETE"),
            a("WORKSPACE_ROLE_MANAGEMENT", "ASSIGN_ROLE"),
            // Workspace member management
            a("WORKSPACE_MEMBER_MANAGEMENT", "VIEW"),
            a("WORKSPACE_MEMBER_MANAGEMENT", "ADD"),
            a("WORKSPACE_MEMBER_MANAGEMENT", "REMOVE"),
            // Team management
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "CREATE"),
            a("TEAM_MANAGEMENT", "UPDATE"),
            a("TEAM_MANAGEMENT", "ARCHIVE"),
            a("TEAM_MANAGEMENT", "MANAGE"),
            // Team member management
            a("TEAM_MEMBER_MANAGEMENT", "VIEW"),
            a("TEAM_MEMBER_MANAGEMENT", "ADD"),
            a("TEAM_MEMBER_MANAGEMENT", "REMOVE"),
            // Knowledge: document type management
            a("DOCUMENT_TYPE_MANAGEMENT", "VIEW"),
            a("DOCUMENT_TYPE_MANAGEMENT", "CREATE"),
            a("DOCUMENT_TYPE_MANAGEMENT", "UPDATE"),
            a("DOCUMENT_TYPE_MANAGEMENT", "ARCHIVE"),
            a("DOCUMENT_TYPE_MANAGEMENT", "MANAGE"),
            // Knowledge: document type field management
            a("DOCUMENT_TYPE_FIELD_MANAGEMENT", "VIEW"),
            a("DOCUMENT_TYPE_FIELD_MANAGEMENT", "CREATE"),
            a("DOCUMENT_TYPE_FIELD_MANAGEMENT", "UPDATE"),
            a("DOCUMENT_TYPE_FIELD_MANAGEMENT", "ARCHIVE"),
            a("DOCUMENT_TYPE_FIELD_MANAGEMENT", "MANAGE"),
            // Knowledge: classification
            a("KNOWLEDGE_CLASSIFICATION_MANAGEMENT", "VIEW"),
            // Phase definition management
            a("PHASE_DEFINITION_MANAGEMENT", "VIEW"),
            a("PHASE_DEFINITION_MANAGEMENT", "CREATE"),
            a("PHASE_DEFINITION_MANAGEMENT", "UPDATE"),
            a("PHASE_DEFINITION_MANAGEMENT", "ARCHIVE"),
            a("PHASE_DEFINITION_MANAGEMENT", "MANAGE"),
            // Project template management
            a("PROJECT_TEMPLATE_MANAGEMENT", "VIEW"),
            a("PROJECT_TEMPLATE_MANAGEMENT", "CREATE"),
            a("PROJECT_TEMPLATE_MANAGEMENT", "UPDATE"),
            a("PROJECT_TEMPLATE_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_TEMPLATE_MANAGEMENT", "APPLY"),
            a("PROJECT_TEMPLATE_MANAGEMENT", "MANAGE"),
            // Project management
            a("PROJECT_MANAGEMENT", "VIEW"),
            a("PROJECT_MANAGEMENT", "CREATE"),
            a("PROJECT_MANAGEMENT", "UPDATE"),
            a("PROJECT_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_MANAGEMENT", "MANAGE"),
            // Project phase management
            a("PROJECT_PHASE_MANAGEMENT", "VIEW"),
            a("PROJECT_PHASE_MANAGEMENT", "CREATE"),
            a("PROJECT_PHASE_MANAGEMENT", "UPDATE"),
            a("PROJECT_PHASE_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_PHASE_MANAGEMENT", "MANAGE"),
            // Project WBS management
            a("PROJECT_WBS_MANAGEMENT", "VIEW"),
            a("PROJECT_WBS_MANAGEMENT", "CREATE"),
            a("PROJECT_WBS_MANAGEMENT", "UPDATE"),
            a("PROJECT_WBS_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_WBS_MANAGEMENT", "MANAGE"),
            // Project task management
            a("PROJECT_TASK_MANAGEMENT", "VIEW"),
            a("PROJECT_TASK_MANAGEMENT", "CREATE"),
            a("PROJECT_TASK_MANAGEMENT", "UPDATE"),
            a("PROJECT_TASK_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_TASK_MANAGEMENT", "MANAGE"),
            // Capacity: calendar management
            a("CAPACITY_CALENDAR_MANAGEMENT", "VIEW"),
            a("CAPACITY_CALENDAR_MANAGEMENT", "CREATE"),
            a("CAPACITY_CALENDAR_MANAGEMENT", "UPDATE"),
            a("CAPACITY_CALENDAR_MANAGEMENT", "ARCHIVE"),
            a("CAPACITY_CALENDAR_MANAGEMENT", "MANAGE"),
            // Capacity: profile management
            a("CAPACITY_PROFILE_MANAGEMENT", "VIEW"),
            a("CAPACITY_PROFILE_MANAGEMENT", "CREATE"),
            a("CAPACITY_PROFILE_MANAGEMENT", "UPDATE"),
            a("CAPACITY_PROFILE_MANAGEMENT", "ARCHIVE"),
            a("CAPACITY_PROFILE_MANAGEMENT", "MANAGE"),
            // Capacity: project allocation management
            a("PROJECT_ALLOCATION_MANAGEMENT", "VIEW"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "CREATE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "UPDATE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "MANAGE"),
            // Capacity: resource capacity overview
            a("CAPACITY_MANAGEMENT", "VIEW"),
            a("CAPACITY_MANAGEMENT", "CALCULATE")
    );

    private static final List<IamOwnerPolicyAction> TEAM_ACTIONS = List.of(
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "MANAGE"),
            a("TEAM_MEMBER_MANAGEMENT", "VIEW"),
            a("TEAM_MEMBER_MANAGEMENT", "ADD"),
            a("TEAM_MEMBER_MANAGEMENT", "REMOVE")
    );
}
