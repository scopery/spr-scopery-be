package com.company.scopery.modules.iam.integration;

import com.company.scopery.modules.iam.authorization.application.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.IamResourceVisibility;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightCode;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Bootstraps IAM auth resources and owner access grants when workspace entities are created.
 * Called from Workspace module application services within the same transaction.
 */
@Service
public class WorkspaceIamIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceIamIntegrationService.class);

    private static final List<String> ORG_OWNER_RIGHTS =
            List.of("VIEW_ORGANIZATION", "MANAGE_ORGANIZATION", "CREATE_WORKSPACE");
    private static final List<String> WORKSPACE_OWNER_RIGHTS =
            List.of("VIEW_WORKSPACE", "MANAGE_WORKSPACE", "MANAGE_MEMBER",
                    "MANAGE_TEAM", "MANAGE_ROLE", "MANAGE_ACCESS", "MANAGE_WORKSPACE_SETTING",
                    "MANAGE_PERMISSION",
                    "VIEW_ROLE", "CREATE_ROLE", "UPDATE_ROLE", "DELETE_ROLE", "ASSIGN_ROLE",
                    "VIEW_DOCUMENT_TYPE", "CREATE_DOCUMENT_TYPE", "UPDATE_DOCUMENT_TYPE",
                    "DELETE_DOCUMENT_TYPE", "MANAGE_DOCUMENT_TYPE",
                    "WORKSPACE_INVITE_MEMBER", "WORKSPACE_MANAGE_INVITATION",
                    "WORKSPACE_MANAGE_JOIN_REQUEST");
    private static final List<String> TEAM_OWNER_RIGHTS =
            List.of("VIEW_TEAM", "MANAGE_TEAM");

    private final IamAuthResourceRepository authResourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamRightRepository rightRepository;
    private final AuthorizationDecisionService decisionService;

    public WorkspaceIamIntegrationService(IamAuthResourceRepository authResourceRepository,
                                           IamAccessGrantRepository grantRepository,
                                           IamAccessGrantRightRepository grantRightRepository,
                                           IamRightRepository rightRepository,
                                           AuthorizationDecisionService decisionService) {
        this.authResourceRepository = authResourceRepository;
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.rightRepository = rightRepository;
        this.decisionService = decisionService;
    }

    public void requireOrgAccess(UUID orgId, UUID userId, String rightCode) {
        authResourceRepository.findByRefIdAndResourceType(orgId, IamResourceType.ORGANIZATION)
                .ifPresent(resource -> decisionService.requireAccess(
                        new AuthorizationRequest(userId, resource.id(), rightCode)));
    }

    public void requireWorkspaceAccess(UUID workspaceId, UUID userId, String rightCode) {
        authResourceRepository.findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                .ifPresent(resource -> decisionService.requireAccess(
                        new AuthorizationRequest(userId, resource.id(), rightCode)));
    }

    public UUID bootstrapOrganizationAccess(UUID orgId, String orgName, UUID ownerUserId) {
        try {
            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("ORG:" + orgId),
                            IamResourceType.ORGANIZATION,
                            orgName, null,
                            orgId, ownerUserId, null,
                            IamResourceVisibility.PRIVATE, null));

            createOwnerGrant(resource.id(), ownerUserId, ORG_OWNER_RIGHTS, "ORGANIZATION", orgId);
            log.info("IAM bootstrap: org={} resource={} owner={}", orgId, resource.id(), ownerUserId);
            return resource.id();
        } catch (Exception e) {
            throw IamExceptions.iamResourceBootstrapFailed("ORGANIZATION", orgId, e.getMessage());
        }
    }

    public UUID bootstrapWorkspaceAccess(UUID workspaceId, String workspaceName, UUID ownerUserId) {
        try {
            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("WS:" + workspaceId),
                            IamResourceType.WORKSPACE,
                            workspaceName, null,
                            workspaceId, ownerUserId, workspaceId,
                            IamResourceVisibility.PRIVATE, null));

            createOwnerGrant(resource.id(), ownerUserId, WORKSPACE_OWNER_RIGHTS, "WORKSPACE", workspaceId);
            log.info("IAM bootstrap: workspace={} resource={} owner={}", workspaceId, resource.id(), ownerUserId);
            return resource.id();
        } catch (Exception e) {
            throw IamExceptions.iamResourceBootstrapFailed("WORKSPACE", workspaceId, e.getMessage());
        }
    }

    public UUID bootstrapTeamAccess(UUID teamId, UUID workspaceId, String teamName, UUID ownerUserId) {
        try {
            UUID wsResourceId = authResourceRepository
                    .findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                    .map(IamAuthResource::id)
                    .orElse(null);

            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("TEAM:" + teamId),
                            IamResourceType.TEAM,
                            teamName, null,
                            teamId, ownerUserId, workspaceId,
                            IamResourceVisibility.PRIVATE, wsResourceId));

            createOwnerGrant(resource.id(), ownerUserId, TEAM_OWNER_RIGHTS, "TEAM", teamId);
            log.info("IAM bootstrap: team={} resource={} owner={}", teamId, resource.id(), ownerUserId);
            return resource.id();
        } catch (Exception e) {
            throw IamExceptions.iamResourceBootstrapFailed("TEAM", teamId, e.getMessage());
        }
    }

    private void createOwnerGrant(UUID resourceId, UUID ownerUserId, List<String> rightCodes,
                                   String entityType, UUID entityId) {
        try {
            IamAccessGrant grant = grantRepository.save(
                    IamAccessGrant.create(IamSubjectType.USER, ownerUserId, resourceId,
                            null, IamGrantEffect.ALLOW, null, null, null, ownerUserId));

            for (String rightCode : rightCodes) {
                IamRight right = rightRepository.findByCode(IamRightCode.of(rightCode))
                        .orElseThrow(() -> IamExceptions.iamRightRequiredForBootstrapNotFound(rightCode));
                grantRightRepository.save(IamAccessGrantRight.create(grant.id(), right.id()));
            }
        } catch (Exception e) {
            throw IamExceptions.iamOwnerGrantBootstrapFailed(entityType, entityId, e.getMessage());
        }
    }
}
