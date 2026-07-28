package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.constant.IamActionCodes;
import com.company.scopery.modules.iam.shared.constant.IamPermissionCodes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Ensures a workspace member has contributor baseline actions on a PROJECT IAM resource.
 * Idempotent: creates DIRECT grant when missing, otherwise attaches missing actions.
 */
@Component
public class EnsureProjectMemberBaselineAccessAction {

    private static final Logger log = LoggerFactory.getLogger(EnsureProjectMemberBaselineAccessAction.class);

    private record BaselineAction(String permissionCode, String actionCode) {}

    /**
     * Project-scoped contributor baseline (mirrors workspace delivery baseline without shell/productivity).
     */
    private static final List<BaselineAction> BASELINE_ACTIONS = List.of(
            new BaselineAction(IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.DASHBOARD_VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_VIEW)
    );

    private final IamAuthResourceRepository authResourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;

    public EnsureProjectMemberBaselineAccessAction(
            IamAuthResourceRepository authResourceRepository,
            IamAccessGrantRepository grantRepository,
            IamAccessGrantPermissionActionRepository grantActionRepository,
            IamPermissionRepository permissionRepository,
            IamPermissionActionDefinitionRepository actionRepository) {
        this.authResourceRepository = authResourceRepository;
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public void execute(UUID projectId, UUID userId) {
        IamAuthResource resource = authResourceRepository
                .findByRefIdAndResourceType(projectId, IamResourceType.PROJECT)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(projectId));

        List<IamAccessGrant> existing = grantRepository.findActiveBySubjectsAndResource(
                List.of(IamSubjectType.USER), List.of(userId), resource.id());

        IamAccessGrant grant = existing.stream()
                .filter(g -> g.effect() == IamGrantEffect.ALLOW)
                .findFirst()
                .orElseGet(() -> createDirectGrant(resource, userId));

        attachBaselineActions(grant.id());
    }

    private IamAccessGrant createDirectGrant(IamAuthResource resource, UUID userId) {
        IamAccessGrant created = grantRepository.save(IamAccessGrant.createWithMetadata(
                IamSubjectType.USER,
                userId,
                resource.id(),
                null,
                IamGrantEffect.ALLOW,
                null,
                null,
                resource.workspaceId(),
                IamGrantKind.DIRECT,
                null,
                false,
                0,
                null,
                null,
                "Project member baseline access",
                userId));
        log.info("Created DIRECT project baseline grant {} for user {} on project {}",
                created.id(), userId, resource.refId());
        return created;
    }

    private void attachBaselineActions(UUID grantId) {
        for (BaselineAction baseline : BASELINE_ACTIONS) {
            IamPermission permission = permissionRepository
                    .findByCode(IamPermissionCode.of(baseline.permissionCode()))
                    .orElse(null);
            if (permission == null) {
                log.warn("Skipping missing permission {}", baseline.permissionCode());
                continue;
            }
            IamPermissionActionDefinition action = actionRepository
                    .findByPermissionIdAndActionCode(permission.id(), baseline.actionCode())
                    .orElse(null);
            if (action == null) {
                log.warn("Skipping missing action {} on permission {}",
                        baseline.actionCode(), baseline.permissionCode());
                continue;
            }
            if (grantActionRepository.existsByGrantIdAndPermissionActionId(grantId, action.id())) {
                continue;
            }
            grantActionRepository.save(IamAccessGrantPermissionAction.create(grantId, action.id()));
        }
    }
}
