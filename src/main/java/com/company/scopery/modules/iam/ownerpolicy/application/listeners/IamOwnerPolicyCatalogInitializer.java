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
 * Idempotent ensure of ORGANIZATION / WORKSPACE / TEAM / PROJECT owner policies (complements V36 seed).
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
        ensure(IamResourceType.WORKSPACE,    IamInheritanceScope.DESCENDANTS, 1, 4, WORKSPACE_ACTIONS);
        ensure(IamResourceType.TEAM,         IamInheritanceScope.SELF_ONLY,   1, 1, TEAM_ACTIONS);
        ensure(IamResourceType.PROJECT,      IamInheritanceScope.SELF_ONLY,   1, 1, PROJECT_ACTIONS);
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
            a("CAPACITY_MANAGEMENT", "CALCULATE"),
            // Productivity: personal inbox / search / favorites (every workspace user needs this)
            a("PRODUCTIVITY_MANAGEMENT", "VIEW"),
            a("PRODUCTIVITY_MANAGEMENT", "CREATE"),
            a("PRODUCTIVITY_MANAGEMENT", "MANAGE"),
            // Delivery modules (owner = full)
            a("SCOPE_MANAGEMENT", "VIEW"),
            a("SCOPE_MANAGEMENT", "CREATE"),
            a("SCOPE_MANAGEMENT", "UPDATE"),
            a("SCOPE_MANAGEMENT", "APPROVE"),
            a("SCOPE_MANAGEMENT", "ARCHIVE"),
            a("DELIVERABLE_MANAGEMENT", "VIEW"),
            a("DELIVERABLE_MANAGEMENT", "CREATE"),
            a("DELIVERABLE_MANAGEMENT", "UPDATE"),
            a("DELIVERABLE_MANAGEMENT", "SUBMIT_REVIEW"),
            a("DELIVERABLE_MANAGEMENT", "ACCEPT"),
            a("DELIVERABLE_MANAGEMENT", "REJECT"),
            a("DELIVERABLE_MANAGEMENT", "ARCHIVE"),
            a("REQUIREMENT_MANAGEMENT", "VIEW"),
            a("REQUIREMENT_MANAGEMENT", "CREATE"),
            a("REQUIREMENT_MANAGEMENT", "UPDATE"),
            a("REQUIREMENT_MANAGEMENT", "APPROVE"),
            a("DOCUMENT_HUB_MANAGEMENT", "VIEW"),
            a("DOCUMENT_HUB_MANAGEMENT", "CREATE"),
            a("DOCUMENT_HUB_MANAGEMENT", "UPDATE"),
            a("DOCUMENT_HUB_MANAGEMENT", "APPROVE"),
            a("RAID_MANAGEMENT", "VIEW"),
            a("RAID_MANAGEMENT", "CREATE"),
            a("RAID_MANAGEMENT", "UPDATE"),
            a("RAID_MANAGEMENT", "ARCHIVE"),
            a("DECISION_MANAGEMENT", "VIEW"),
            a("DECISION_MANAGEMENT", "CREATE"),
            a("DECISION_MANAGEMENT", "UPDATE"),
            a("DECISION_MANAGEMENT", "DECIDE"),
            a("DECISION_MANAGEMENT", "ARCHIVE"),
            a("COLLABORATION_MANAGEMENT", "VIEW"),
            a("COLLABORATION_MANAGEMENT", "CREATE"),
            a("COLLABORATION_MANAGEMENT", "UPDATE"),
            a("COLLABORATION_MANAGEMENT", "MANAGE"),
            a("COMMENT_MANAGEMENT", "VIEW"),
            a("COMMENT_MANAGEMENT", "CREATE"),
            a("COMMENT_MANAGEMENT", "UPDATE"),
            a("REPORTING_MANAGEMENT", "DASHBOARD_VIEW"),
            a("REPORTING_MANAGEMENT", "REPORT_VIEW"),
            a("REPORTING_MANAGEMENT", "REPORT_RUN"),
            a("REPORTING_MANAGEMENT", "REPORT_EXPORT"),
            a("REPORTING_MANAGEMENT", "HEALTH_VIEW"),
            a("REPORTING_MANAGEMENT", "KPI_VIEW"),
            // Commercial
            a("ESTIMATION_MANAGEMENT", "VIEW"),
            a("PROJECT_FINANCE_MANAGEMENT", "VIEW"),
            a("PROJECT_FINANCE_MANAGEMENT", "CREATE"),
            a("PROJECT_FINANCE_MANAGEMENT", "UPDATE"),
            a("PROJECT_FINANCE_MANAGEMENT", "APPROVE"),
            a("PROJECT_FINANCE_MANAGEMENT", "MANAGE"),
            a("QUOTE_MANAGEMENT", "VIEW"),
            a("QUOTE_MANAGEMENT", "CREATE"),
            a("QUOTE_MANAGEMENT", "UPDATE"),
            a("QUOTE_MANAGEMENT", "APPROVE"),
            a("QUOTE_MANAGEMENT", "MANAGE"),
            a("PROFITABILITY_MANAGEMENT", "VIEW"),
            a("PROFITABILITY_MANAGEMENT", "VIEW_SUMMARY"),
            a("PROFITABILITY_MANAGEMENT", "UPDATE"),
            // Quality
            a("QUALITY_MANAGEMENT", "VIEW"),
            a("QUALITY_MANAGEMENT", "CREATE"),
            a("QUALITY_MANAGEMENT", "UPDATE"),
            a("QUALITY_MANAGEMENT", "APPROVE"),
            a("TEST_MANAGEMENT", "VIEW"),
            a("TEST_MANAGEMENT", "CREATE"),
            a("TEST_MANAGEMENT", "UPDATE"),
            a("TEST_MANAGEMENT", "EXECUTE"),
            a("DEFECT_MANAGEMENT", "VIEW"),
            a("DEFECT_MANAGEMENT", "CREATE"),
            a("DEFECT_MANAGEMENT", "UPDATE"),
            a("DEFECT_MANAGEMENT", "RESOLVE"),
            a("RELEASE_MANAGEMENT", "VIEW"),
            a("RELEASE_MANAGEMENT", "CREATE"),
            a("RELEASE_MANAGEMENT", "UPDATE"),
            a("RELEASE_MANAGEMENT", "APPROVE"),
            // Control
            a("PROJECT_BASELINE_MANAGEMENT", "VIEW"),
            a("PROJECT_BASELINE_MANAGEMENT", "CREATE"),
            a("PROJECT_BASELINE_MANAGEMENT", "UPDATE"),
            a("PROJECT_BASELINE_MANAGEMENT", "APPROVE"),
            a("CHANGE_REQUEST_MANAGEMENT", "VIEW"),
            a("CHANGE_REQUEST_MANAGEMENT", "CREATE"),
            a("CHANGE_REQUEST_MANAGEMENT", "UPDATE"),
            a("CHANGE_REQUEST_MANAGEMENT", "APPROVE"),
            // Clients / AI / governance
            a("EXTERNAL_PARTY_MANAGEMENT", "VIEW"),
            a("EXTERNAL_PARTY_MANAGEMENT", "CREATE"),
            a("EXTERNAL_PARTY_MANAGEMENT", "UPDATE"),
            a("CLIENT_PORTAL_MANAGEMENT", "VIEW"),
            a("CLIENT_PORTAL_MANAGEMENT", "MANAGE"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "VIEW"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "RUN"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "REVIEW"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "ACCEPT"),
            a("OBJECT_GOVERNANCE_MANAGEMENT", "VIEW"),
            a("OBJECT_GOVERNANCE_MANAGEMENT", "ASSIGN")
    );

    private static final List<IamOwnerPolicyAction> TEAM_ACTIONS = List.of(
            a("TEAM_MANAGEMENT", "VIEW"),
            a("TEAM_MANAGEMENT", "MANAGE"),
            a("TEAM_MEMBER_MANAGEMENT", "VIEW"),
            a("TEAM_MEMBER_MANAGEMENT", "ADD"),
            a("TEAM_MEMBER_MANAGEMENT", "REMOVE")
    );

    /** Project owner: delivery + commercial + quality + control on the PROJECT resource. */
    private static final List<IamOwnerPolicyAction> PROJECT_ACTIONS = List.of(
            a("WORKSPACE_MEMBER_MANAGEMENT", "VIEW"),
            a("PROJECT_MANAGEMENT", "VIEW"),
            a("PROJECT_MANAGEMENT", "CREATE"),
            a("PROJECT_MANAGEMENT", "UPDATE"),
            a("PROJECT_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_MANAGEMENT", "MANAGE"),
            a("PROJECT_PHASE_MANAGEMENT", "VIEW"),
            a("PROJECT_PHASE_MANAGEMENT", "CREATE"),
            a("PROJECT_PHASE_MANAGEMENT", "UPDATE"),
            a("PROJECT_PHASE_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_PHASE_MANAGEMENT", "MANAGE"),
            a("PROJECT_WBS_MANAGEMENT", "VIEW"),
            a("PROJECT_WBS_MANAGEMENT", "CREATE"),
            a("PROJECT_WBS_MANAGEMENT", "UPDATE"),
            a("PROJECT_WBS_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_WBS_MANAGEMENT", "MANAGE"),
            a("PROJECT_TASK_MANAGEMENT", "VIEW"),
            a("PROJECT_TASK_MANAGEMENT", "CREATE"),
            a("PROJECT_TASK_MANAGEMENT", "UPDATE"),
            a("PROJECT_TASK_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_TASK_MANAGEMENT", "MANAGE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "VIEW"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "CREATE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "UPDATE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "ARCHIVE"),
            a("PROJECT_ALLOCATION_MANAGEMENT", "MANAGE"),
            a("SCOPE_MANAGEMENT", "VIEW"),
            a("SCOPE_MANAGEMENT", "CREATE"),
            a("SCOPE_MANAGEMENT", "UPDATE"),
            a("SCOPE_MANAGEMENT", "APPROVE"),
            a("SCOPE_MANAGEMENT", "ARCHIVE"),
            a("DELIVERABLE_MANAGEMENT", "VIEW"),
            a("DELIVERABLE_MANAGEMENT", "CREATE"),
            a("DELIVERABLE_MANAGEMENT", "UPDATE"),
            a("DELIVERABLE_MANAGEMENT", "SUBMIT_REVIEW"),
            a("DELIVERABLE_MANAGEMENT", "ACCEPT"),
            a("DELIVERABLE_MANAGEMENT", "REJECT"),
            a("DELIVERABLE_MANAGEMENT", "ARCHIVE"),
            a("REQUIREMENT_MANAGEMENT", "VIEW"),
            a("REQUIREMENT_MANAGEMENT", "CREATE"),
            a("REQUIREMENT_MANAGEMENT", "UPDATE"),
            a("REQUIREMENT_MANAGEMENT", "APPROVE"),
            a("DOCUMENT_HUB_MANAGEMENT", "VIEW"),
            a("DOCUMENT_HUB_MANAGEMENT", "CREATE"),
            a("DOCUMENT_HUB_MANAGEMENT", "UPDATE"),
            a("DOCUMENT_HUB_MANAGEMENT", "APPROVE"),
            a("RAID_MANAGEMENT", "VIEW"),
            a("RAID_MANAGEMENT", "CREATE"),
            a("RAID_MANAGEMENT", "UPDATE"),
            a("RAID_MANAGEMENT", "ARCHIVE"),
            a("DECISION_MANAGEMENT", "VIEW"),
            a("DECISION_MANAGEMENT", "CREATE"),
            a("DECISION_MANAGEMENT", "UPDATE"),
            a("DECISION_MANAGEMENT", "DECIDE"),
            a("DECISION_MANAGEMENT", "ARCHIVE"),
            a("COLLABORATION_MANAGEMENT", "VIEW"),
            a("COLLABORATION_MANAGEMENT", "CREATE"),
            a("COLLABORATION_MANAGEMENT", "UPDATE"),
            a("COLLABORATION_MANAGEMENT", "MANAGE"),
            a("COMMENT_MANAGEMENT", "VIEW"),
            a("COMMENT_MANAGEMENT", "CREATE"),
            a("COMMENT_MANAGEMENT", "UPDATE"),
            a("REPORTING_MANAGEMENT", "DASHBOARD_VIEW"),
            a("REPORTING_MANAGEMENT", "REPORT_VIEW"),
            a("REPORTING_MANAGEMENT", "REPORT_RUN"),
            a("REPORTING_MANAGEMENT", "REPORT_EXPORT"),
            a("REPORTING_MANAGEMENT", "HEALTH_VIEW"),
            a("REPORTING_MANAGEMENT", "KPI_VIEW"),
            a("ESTIMATION_MANAGEMENT", "VIEW"),
            a("PROJECT_FINANCE_MANAGEMENT", "VIEW"),
            a("PROJECT_FINANCE_MANAGEMENT", "CREATE"),
            a("PROJECT_FINANCE_MANAGEMENT", "UPDATE"),
            a("PROJECT_FINANCE_MANAGEMENT", "APPROVE"),
            a("PROJECT_FINANCE_MANAGEMENT", "MANAGE"),
            a("QUOTE_MANAGEMENT", "VIEW"),
            a("QUOTE_MANAGEMENT", "CREATE"),
            a("QUOTE_MANAGEMENT", "UPDATE"),
            a("QUOTE_MANAGEMENT", "APPROVE"),
            a("QUOTE_MANAGEMENT", "MANAGE"),
            a("PROFITABILITY_MANAGEMENT", "VIEW"),
            a("PROFITABILITY_MANAGEMENT", "VIEW_SUMMARY"),
            a("PROFITABILITY_MANAGEMENT", "UPDATE"),
            a("QUALITY_MANAGEMENT", "VIEW"),
            a("QUALITY_MANAGEMENT", "CREATE"),
            a("QUALITY_MANAGEMENT", "UPDATE"),
            a("QUALITY_MANAGEMENT", "APPROVE"),
            a("TEST_MANAGEMENT", "VIEW"),
            a("TEST_MANAGEMENT", "CREATE"),
            a("TEST_MANAGEMENT", "UPDATE"),
            a("TEST_MANAGEMENT", "EXECUTE"),
            a("DEFECT_MANAGEMENT", "VIEW"),
            a("DEFECT_MANAGEMENT", "CREATE"),
            a("DEFECT_MANAGEMENT", "UPDATE"),
            a("DEFECT_MANAGEMENT", "RESOLVE"),
            a("RELEASE_MANAGEMENT", "VIEW"),
            a("RELEASE_MANAGEMENT", "CREATE"),
            a("RELEASE_MANAGEMENT", "UPDATE"),
            a("RELEASE_MANAGEMENT", "APPROVE"),
            a("PROJECT_BASELINE_MANAGEMENT", "VIEW"),
            a("PROJECT_BASELINE_MANAGEMENT", "CREATE"),
            a("PROJECT_BASELINE_MANAGEMENT", "UPDATE"),
            a("PROJECT_BASELINE_MANAGEMENT", "APPROVE"),
            a("CHANGE_REQUEST_MANAGEMENT", "VIEW"),
            a("CHANGE_REQUEST_MANAGEMENT", "CREATE"),
            a("CHANGE_REQUEST_MANAGEMENT", "UPDATE"),
            a("CHANGE_REQUEST_MANAGEMENT", "APPROVE"),
            a("EXTERNAL_PARTY_MANAGEMENT", "VIEW"),
            a("CLIENT_PORTAL_MANAGEMENT", "VIEW"),
            a("CLIENT_PORTAL_MANAGEMENT", "MANAGE"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "VIEW"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "RUN"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "REVIEW"),
            a("AI_PROJECT_PLANNING_MANAGEMENT", "ACCEPT"),
            a("OBJECT_GOVERNANCE_MANAGEMENT", "VIEW"),
            a("OBJECT_GOVERNANCE_MANAGEMENT", "ASSIGN")
    );
}
