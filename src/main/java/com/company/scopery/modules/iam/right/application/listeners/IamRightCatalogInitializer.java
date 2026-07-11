package com.company.scopery.modules.iam.right.application.listeners;

import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(100)
public class IamRightCatalogInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamRightCatalogInitializer.class);

    private final IamRightRepository rightRepository;

    public IamRightCatalogInitializer(IamRightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<RightDef> catalog = buildCatalog();
        int seeded = 0;
        for (RightDef def : catalog) {
            IamRightCode code = IamRightCode.of(def.code);
            if (!rightRepository.existsByCode(code)) {
                rightRepository.save(IamRight.create(code, def.name, def.description, def.module));
                seeded++;
            }
        }
        if (seeded > 0) {
            log.info("IAM right catalog: seeded {} rights", seeded);
        }
    }

    private List<RightDef> buildCatalog() {
        return List.of(
                // ORGANIZATION
                new RightDef("VIEW_ORGANIZATION",    "View Organization",    "View organization details",            "ORGANIZATION"),
                new RightDef("CREATE_ORGANIZATION",  "Create Organization",  "Create a new organization",            "ORGANIZATION"),
                new RightDef("UPDATE_ORGANIZATION",  "Update Organization",  "Update organization name/description", "ORGANIZATION"),
                new RightDef("ARCHIVE_ORGANIZATION", "Archive Organization", "Archive an organization",              "ORGANIZATION"),
                new RightDef("MANAGE_ORGANIZATION",  "Manage Organization",  "Full management of an organization",   "ORGANIZATION"),
                // WORKSPACE
                new RightDef("VIEW_WORKSPACE",             "View Workspace",             "View workspace details",                    "WORKSPACE"),
                new RightDef("CREATE_WORKSPACE",           "Create Workspace",           "Create a new workspace",                    "WORKSPACE"),
                new RightDef("UPDATE_WORKSPACE",           "Update Workspace",           "Update workspace settings",                 "WORKSPACE"),
                new RightDef("ARCHIVE_WORKSPACE",          "Archive Workspace",          "Archive a workspace",                       "WORKSPACE"),
                new RightDef("MANAGE_WORKSPACE",           "Manage Workspace",           "Full management of a workspace",            "WORKSPACE"),
                new RightDef("MANAGE_MEMBER",              "Manage Member",              "Add, remove, and manage workspace members", "WORKSPACE"),
                new RightDef("MANAGE_TEAM",                "Manage Team",                "Create, update, and manage teams",          "WORKSPACE"),
                new RightDef("MANAGE_ROLE",                "Manage Role Assignments",    "Assign and revoke roles in workspace",      "WORKSPACE"),
                new RightDef("MANAGE_ACCESS",              "Manage Access Grants",       "Create and revoke access grants",           "WORKSPACE"),
                new RightDef("MANAGE_WORKSPACE_SETTING",   "Manage Workspace Settings",  "Manage workspace-level settings",           "WORKSPACE"),
                new RightDef("MANAGE_PERMISSION",          "Manage Permissions",         "Manage workspace-level permissions",        "WORKSPACE"),
                // TEAM
                new RightDef("VIEW_TEAM",    "View Team",    "View team details and members",      "TEAM"),
                new RightDef("CREATE_TEAM",  "Create Team",  "Create a new team in a workspace",   "TEAM"),
                new RightDef("UPDATE_TEAM",  "Update Team",  "Update team name/description",       "TEAM"),
                new RightDef("ARCHIVE_TEAM", "Archive Team", "Archive a team",                     "TEAM"),
                // WORKSPACE_MEMBER
                new RightDef("VIEW_WORKSPACE_MEMBER",   "View Workspace Members",  "View workspace member list",        "WORKSPACE_MEMBER"),
                new RightDef("ADD_WORKSPACE_MEMBER",    "Add Workspace Member",    "Add a user to a workspace",         "WORKSPACE_MEMBER"),
                new RightDef("REMOVE_WORKSPACE_MEMBER", "Remove Workspace Member", "Remove a user from a workspace",    "WORKSPACE_MEMBER"),
                // TEAM_MEMBER
                new RightDef("VIEW_TEAM_MEMBER",   "View Team Members",  "View team member list",          "TEAM_MEMBER"),
                new RightDef("ADD_TEAM_MEMBER",    "Add Team Member",    "Add a user to a team",           "TEAM_MEMBER"),
                new RightDef("REMOVE_TEAM_MEMBER", "Remove Team Member", "Remove a user from a team",      "TEAM_MEMBER"),
                // IAM
                new RightDef("VIEW_IAM_USER",      "View User",          "View user profile",                      "IAM"),
                new RightDef("CREATE_IAM_USER",    "Create User",        "Create a new user account",              "IAM"),
                new RightDef("MANAGE_IAM_USER",    "Manage User",        "Activate, deactivate, suspend, and update user accounts", "IAM"),
                new RightDef("VIEW_IAM_ROLE",      "View Role",          "View role details",                      "IAM"),
                new RightDef("MANAGE_IAM_ROLE",    "Manage Role",        "Create, update, activate/deactivate roles","IAM"),
                new RightDef("VIEW_IAM_RIGHT",     "View Right",         "View right catalog entries",             "IAM"),
                new RightDef("MANAGE_ACCESS_GRANT","Manage Access Grant","Create and revoke access grants",        "IAM"),
                new RightDef("VIEW_ACCESS_GRANT",  "View Access Grant",  "View access grants for subjects",        "IAM"),
                new RightDef("VIEW_IAM_RESOURCE",  "View IAM Resource",  "View IAM protected resource registry",    "IAM"),
                new RightDef("MANAGE_IAM_RESOURCE","Manage IAM Resource","Manage IAM protected resource registry",  "IAM"),
                // SYSTEM GOVERNANCE
                new RightDef("SYSTEM_MANAGE_ROLE",          "System Manage Role",          "Create, update, and delete system roles",         "GOVERNANCE"),
                new RightDef("SYSTEM_MANAGE_DOCUMENT_TYPE", "System Manage Document Type", "Create, update, and delete system document types", "GOVERNANCE"),
                new RightDef("SYSTEM_VIEW_GOVERNANCE",      "System View Governance",      "View system governance configuration",            "GOVERNANCE"),
                new RightDef("SYSTEM_MANAGE_GOVERNANCE",    "System Manage Governance",    "Full system governance management",               "GOVERNANCE"),
                // WORKSPACE ROLE MANAGEMENT
                new RightDef("VIEW_ROLE",   "View Role",   "View workspace roles",           "ROLE"),
                new RightDef("CREATE_ROLE", "Create Role", "Create a workspace role",         "ROLE"),
                new RightDef("UPDATE_ROLE", "Update Role", "Update a workspace role",         "ROLE"),
                new RightDef("DELETE_ROLE", "Delete Role", "Soft-delete a workspace role",    "ROLE"),
                new RightDef("ASSIGN_ROLE", "Assign Role", "Assign a role to a user or team", "ROLE"),
                // DOCUMENT TYPE
                new RightDef("VIEW_DOCUMENT_TYPE",   "View Document Type",   "View document type configuration",              "DOCUMENT_TYPE"),
                new RightDef("CREATE_DOCUMENT_TYPE", "Create Document Type", "Create a workspace document type",              "DOCUMENT_TYPE"),
                new RightDef("UPDATE_DOCUMENT_TYPE", "Update Document Type", "Update a workspace document type",              "DOCUMENT_TYPE"),
                new RightDef("DELETE_DOCUMENT_TYPE", "Delete Document Type", "Soft-delete a workspace document type",         "DOCUMENT_TYPE"),
                new RightDef("MANAGE_DOCUMENT_TYPE", "Manage Document Type", "Full management of workspace document types",   "DOCUMENT_TYPE"),
                // PHASE DEFINITION
                new RightDef("VIEW_PHASE_DEFINITION",    "View Phase Definition",    "View phase definition templates",             "PHASE_DEFINITION"),
                new RightDef("CREATE_PHASE_DEFINITION",  "Create Phase Definition",  "Create a workspace phase definition template", "PHASE_DEFINITION"),
                new RightDef("UPDATE_PHASE_DEFINITION",  "Update Phase Definition",  "Update a workspace phase definition template",  "PHASE_DEFINITION"),
                new RightDef("ARCHIVE_PHASE_DEFINITION", "Archive Phase Definition", "Archive a workspace phase definition template", "PHASE_DEFINITION"),
                new RightDef("MANAGE_PHASE_DEFINITION",  "Manage Phase Definition",  "Full management of workspace phase definitions", "PHASE_DEFINITION"),
                new RightDef("SYSTEM_MANAGE_PHASE_DEFINITION", "System Manage Phase Definition", "Create, update, and archive system phase definitions", "GOVERNANCE"),
                // DOCUMENT ACCESS BY TYPE
                new RightDef("VIEW_DOCUMENT_BY_TYPE",   "View Document by Type",   "View documents of a specific document type",   "DOCUMENT"),
                new RightDef("CREATE_DOCUMENT_BY_TYPE", "Create Document by Type", "Create documents of a specific document type", "DOCUMENT"),
                new RightDef("UPDATE_DOCUMENT_BY_TYPE", "Update Document by Type", "Update documents of a specific document type", "DOCUMENT"),
                new RightDef("DELETE_DOCUMENT_BY_TYPE", "Delete Document by Type", "Delete documents of a specific document type", "DOCUMENT"),
                // WORKSPACE INVITATION / JOIN REQUEST
                new RightDef("WORKSPACE_INVITE_MEMBER",       "Invite Member",        "Create workspace invitation links",     "WORKSPACE"),
                new RightDef("WORKSPACE_MANAGE_INVITATION",   "Manage Invitations",   "View and revoke workspace invitations", "WORKSPACE"),
                new RightDef("WORKSPACE_REQUEST_JOIN",        "Request to Join",      "Submit a join request to a workspace",  "WORKSPACE"),
                new RightDef("WORKSPACE_MANAGE_JOIN_REQUEST", "Manage Join Requests", "Approve or reject join requests",       "WORKSPACE"),
                // SYSTEM NOTIFICATION
                new RightDef("SYSTEM_VIEW_NOTIFICATION",          "System View Notification",          "View notification templates, rules, and deliveries", "NOTIFICATION"),
                new RightDef("SYSTEM_MANAGE_NOTIFICATION",        "System Manage Notification",        "Full notification management access",                "NOTIFICATION"),
                new RightDef("SYSTEM_MANAGE_NOTIFICATION_TEMPLATE","System Manage Notification Template","Create, update, and publish email templates",       "NOTIFICATION"),
                new RightDef("SYSTEM_MANAGE_NOTIFICATION_RULE",   "System Manage Notification Rule",   "Create, update, and configure email rules",          "NOTIFICATION"),
                new RightDef("SYSTEM_VIEW_NOTIFICATION_DELIVERY", "System View Notification Delivery", "View email delivery logs",                           "NOTIFICATION"),
                new RightDef("SYSTEM_RETRY_NOTIFICATION_DELIVERY","System Retry Notification Delivery","Retry failed email outbox entries",                  "NOTIFICATION"),
                // WORKSPACE NOTIFICATION
                new RightDef("VIEW_NOTIFICATION_TEMPLATE",   "View Notification Template",   "View workspace email templates",                        "NOTIFICATION"),
                new RightDef("MANAGE_NOTIFICATION_TEMPLATE", "Manage Notification Template", "Create, update, and publish workspace email templates", "NOTIFICATION"),
                new RightDef("VIEW_NOTIFICATION_RULE",       "View Notification Rule",       "View workspace email rules",                            "NOTIFICATION"),
                new RightDef("MANAGE_NOTIFICATION_RULE",     "Manage Notification Rule",     "Create, update, and configure workspace email rules",   "NOTIFICATION"),
                // EVENT REGISTRY
                new RightDef("SYSTEM_MANAGE_EVENT_REGISTRY", "System Manage Event Registry", "Create, update, activate, and deactivate shared event definitions", "EVENT_REGISTRY"),
                // AI AGENT (enforced by AiAgentSecurityInterceptor via IamSystemAuthorizationService)
                new RightDef("AI_PLATFORM_MANAGE",        "AI Platform Manage",         "Manage AI providers and model deployments",     "AI_AGENT"),
                new RightDef("AI_PROVIDER_SECRET_MANAGE",  "AI Provider Secret Manage",  "Manage AI provider API keys/secrets",           "AI_AGENT"),
                new RightDef("AI_PLAYGROUND_RUN",          "AI Playground Run",         "Run AI playground executions",                  "AI_AGENT"),
                new RightDef("AI_EXECUTION_VIEW_OR_RUN",   "AI Execution View or Run",  "View or trigger AI execution logs",             "AI_AGENT"),
                new RightDef("AI_PROMPT_PUBLISH",          "AI Prompt Publish",         "Publish AI prompt versions",                    "AI_AGENT"),
                new RightDef("AI_EVENT_CONFIG_MANAGE",     "AI Event Config Manage",    "Manage AI Agent event configurations",          "AI_AGENT")
        );
    }

    private record RightDef(String code, String name, String description, String module) {}
}
