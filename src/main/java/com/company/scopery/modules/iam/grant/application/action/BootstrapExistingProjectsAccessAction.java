package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapProjectAccessCommand;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Idempotent bootstrap of PROJECT IAM resources (+ owner + member baselines)
 * for all projects in active workspaces.
 */
@Component
public class BootstrapExistingProjectsAccessAction {

    private static final Logger log = LoggerFactory.getLogger(BootstrapExistingProjectsAccessAction.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final ProjectRepository projectRepository;
    private final BootstrapProjectAccessAction bootstrapProjectAccessAction;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public BootstrapExistingProjectsAccessAction(
            IamAuthResourceRepository authResourceRepository,
            ProjectRepository projectRepository,
            BootstrapProjectAccessAction bootstrapProjectAccessAction,
            @Lazy WorkspaceIamIntegrationService iamIntegrationService) {
        this.authResourceRepository = authResourceRepository;
        this.projectRepository = projectRepository;
        this.bootstrapProjectAccessAction = bootstrapProjectAccessAction;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional
    public int executeAll() {
        int created = 0;
        int memberGrants = 0;
        int failed = 0;
        for (IamAuthResource workspaceResource : authResourceRepository
                .findAllByResourceTypeAndStatus(IamResourceType.WORKSPACE, IamResourceStatus.ACTIVE)) {
            UUID workspaceId = workspaceResource.refId();
            for (Project project : projectRepository.findAllByWorkspaceId(workspaceId)) {
                try {
                    var existing = authResourceRepository.findByRefIdAndResourceType(
                            project.id(), IamResourceType.PROJECT);
                    if (existing.isEmpty()) {
                        UUID ownerId = project.ownerUserId() != null
                                ? project.ownerUserId()
                                : workspaceResource.ownerUserId();
                        bootstrapProjectAccessAction.execute(new BootstrapProjectAccessCommand(
                                project.id(),
                                project.name(),
                                project.workspaceId(),
                                project.organizationId(),
                                ownerId));
                        created++;
                    }
                    iamIntegrationService.ensureProjectMemberBaselinesForProject(project.id(), workspaceId);
                    memberGrants++;
                } catch (Exception ex) {
                    failed++;
                    log.warn("PROJECT IAM bootstrap/member baseline failed for project {}: {}",
                            project.id(), ex.getMessage());
                }
            }
        }
        log.info("[BootstrapExistingProjectsAccess] created={} memberFanOut={} failed={}",
                created, memberGrants, failed);
        return created;
    }
}
