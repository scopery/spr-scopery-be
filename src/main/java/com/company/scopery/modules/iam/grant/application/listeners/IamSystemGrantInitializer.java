package com.company.scopery.modules.iam.grant.application.listeners;

import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.resource.application.listeners.IamSystemAuthResourceInitializer;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Seeds the SUPER_ADMIN role's system-scope grant on the GLOBAL_SYSTEM resource.
 * Must run at @Order(210) — after IamPermissionCatalogInitializer (@Order(200)) — so that
 * permission action definitions exist and can be linked via iam_access_grant_permission_action.
 * Both iam_access_grant_right (legacy path) and iam_access_grant_permission_action (modern path)
 * are seeded so that AuthorizationDecisionService resolves correctly regardless of which path it takes.
 */
@Component
@Order(210)
public class IamSystemGrantInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamSystemGrantInitializer.class);

    private static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";

    private static final List<String> SUPER_ADMIN_SYSTEM_RIGHT_CODES = List.of(
            // AI Agent
            "AI_PLATFORM_MANAGE",
            "AI_PROVIDER_SECRET_MANAGE",
            "AI_PLAYGROUND_RUN",
            "AI_EXECUTION_VIEW_OR_RUN",
            "AI_PROMPT_PUBLISH",
            "AI_EVENT_CONFIG_MANAGE",
            "AI_TOOL_MANAGE",
            "AI_TOOL_EXECUTE",
            // IAM
            "VIEW_IAM_USER",
            "CREATE_IAM_USER",
            "MANAGE_IAM_USER",
            "VIEW_IAM_ROLE",
            "MANAGE_IAM_ROLE",
            "VIEW_IAM_RIGHT",
            "VIEW_IAM_RESOURCE",
            "MANAGE_IAM_RESOURCE",
            "VIEW_ACCESS_GRANT",
            "MANAGE_ACCESS_GRANT",
            // System governance
            "SYSTEM_VIEW_GOVERNANCE",
            "SYSTEM_MANAGE_GOVERNANCE",
            "SYSTEM_MANAGE_ROLE",
            "SYSTEM_MANAGE_DOCUMENT_TYPE",
            "SYSTEM_MANAGE_PHASE_DEFINITION",
            // Notification
            "SYSTEM_VIEW_NOTIFICATION",
            "SYSTEM_MANAGE_NOTIFICATION",
            "SYSTEM_MANAGE_NOTIFICATION_TEMPLATE",
            "SYSTEM_MANAGE_NOTIFICATION_RULE",
            "SYSTEM_VIEW_NOTIFICATION_DELIVERY",
            "SYSTEM_RETRY_NOTIFICATION_DELIVERY",
            // Event registry
            "SYSTEM_MANAGE_EVENT_REGISTRY");

    private final IamRoleRepository roleRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamRightRepository rightRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamPermissionActionDefinitionRepository permissionActionRepository;
    private final IamAccessGrantPermissionActionRepository grantPermissionActionRepository;

    public IamSystemGrantInitializer(IamRoleRepository roleRepository,
                                      IamAuthResourceRepository resourceRepository,
                                      IamRightRepository rightRepository,
                                      IamAccessGrantRepository grantRepository,
                                      IamAccessGrantRightRepository grantRightRepository,
                                      IamPermissionActionDefinitionRepository permissionActionRepository,
                                      IamAccessGrantPermissionActionRepository grantPermissionActionRepository) {
        this.roleRepository = roleRepository;
        this.resourceRepository = resourceRepository;
        this.rightRepository = rightRepository;
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.permissionActionRepository = permissionActionRepository;
        this.grantPermissionActionRepository = grantPermissionActionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        IamRole superAdmin = roleRepository.findByCode(IamRoleCode.of(SUPER_ADMIN_ROLE_CODE)).orElse(null);
        IamAuthResource globalResource = resourceRepository
                .findByCodeAndResourceType(
                        IamResourceCode.of(IamSystemAuthResourceInitializer.GLOBAL_SYSTEM_RESOURCE_CODE),
                        IamResourceType.GLOBAL)
                .orElse(null);
        if (superAdmin == null || globalResource == null) {
            log.warn("IAM system grant: skipped seeding, SUPER_ADMIN role or GLOBAL_SYSTEM resource not found");
            return;
        }

        IamAccessGrant grant;
        if (grantRepository.existsBySubjectIdAndResourceId(superAdmin.id(), globalResource.id())) {
            IamAccessGrant existing = findExistingGrant(superAdmin.id(), globalResource.id());
            if (existing.scopeType() != IamGrantScopeType.GLOBAL_RESOURCE) {
                grant = grantRepository.save(existing.withScopeType(IamGrantScopeType.GLOBAL_RESOURCE));
                log.info("IAM system grant: upgraded SUPER_ADMIN grant {} to GLOBAL_RESOURCE scope", grant.id());
            } else {
                grant = existing;
            }
        } else {
            grant = grantRepository.save(IamAccessGrant.create(
                    IamSubjectType.ROLE, superAdmin.id(), globalResource.id(), superAdmin.id(),
                    IamGrantEffect.ALLOW, IamGrantScopeType.GLOBAL_RESOURCE, globalResource.id(),
                    null, null));
        }

        int rightsSeeded = 0;
        int actionsSeeded = 0;

        for (String code : SUPER_ADMIN_SYSTEM_RIGHT_CODES) {
            IamRight right = rightRepository.findByCode(IamRightCode.of(code)).orElse(null);
            if (right == null) {
                continue;
            }

            // Seed legacy right link (fallback path)
            if (!grantRightRepository.existsByGrantIdAndRightId(grant.id(), right.id())) {
                grantRightRepository.save(IamAccessGrantRight.create(grant.id(), right.id()));
                rightsSeeded++;
            }

            // Seed permission action links (modern path — required when right has mapped actions)
            List<IamPermissionActionDefinition> actions =
                    permissionActionRepository.findByRightIds(List.of(right.id()));
            for (IamPermissionActionDefinition action : actions) {
                if (!grantPermissionActionRepository.existsByGrantIdAndPermissionActionId(grant.id(), action.id())) {
                    grantPermissionActionRepository.save(
                            IamAccessGrantPermissionAction.create(grant.id(), action.id()));
                    actionsSeeded++;
                }
            }
        }

        if (rightsSeeded > 0 || actionsSeeded > 0) {
            log.info("IAM system grant: seeded {} rights and {} permission actions on SUPER_ADMIN global grant {}",
                    rightsSeeded, actionsSeeded, grant.id());
        }
    }

    private IamAccessGrant findExistingGrant(UUID subjectId, UUID resourceId) {
        return grantRepository.findActiveByResource(resourceId).stream()
                .filter(g -> g.subjectType() == IamSubjectType.ROLE && g.subjectId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "IamAccessGrant reported to exist but was not found for subject " + subjectId));
    }
}
