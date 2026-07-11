package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapWorkspaceAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamOwnerGrantCommand;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class BootstrapWorkspaceAccessAction {

    private static final Logger log = LoggerFactory.getLogger(BootstrapWorkspaceAccessAction.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final CreateIamOwnerGrantAction createOwnerGrantAction;

    public BootstrapWorkspaceAccessAction(IamAuthResourceRepository authResourceRepository,
                                          CreateIamOwnerGrantAction createOwnerGrantAction) {
        this.authResourceRepository = authResourceRepository;
        this.createOwnerGrantAction = createOwnerGrantAction;
    }

    @Transactional
    public UUID execute(BootstrapWorkspaceAccessCommand command) {
        try {
            UUID parentResourceId = authResourceRepository
                    .findByRefIdAndResourceType(command.organizationId(), IamResourceType.ORGANIZATION)
                    .map(IamAuthResource::id)
                    .orElseThrow(() -> IamExceptions.iamResourceBootstrapFailed(
                            "ORGANIZATION", command.organizationId(), "Organization IAM resource is missing"));
            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("WS_" + command.workspaceId().toString().replace("-", "").toUpperCase()),
                            IamResourceType.WORKSPACE,
                            command.workspaceName(), null,
                            command.workspaceId(), command.ownerUserId(), command.organizationId(), command.workspaceId(),
                            IamResourceVisibility.PRIVATE, parentResourceId));

            createOwnerGrantAction.execute(new CreateIamOwnerGrantCommand(
                    resource.id(), command.ownerUserId(), IamResourceType.WORKSPACE, "WORKSPACE", command.workspaceId()));
            log.info("IAM bootstrap: workspace={} resource={} owner={}",
                    command.workspaceId(), resource.id(), command.ownerUserId());
            return resource.id();
        } catch (Exception e) {
            log.error("IAM bootstrap failed: WORKSPACE workspaceId={} reason={}", command.workspaceId(), e.getMessage(), e);
            throw IamExceptions.iamResourceBootstrapFailed("WORKSPACE", command.workspaceId(), e.getMessage());
        }
    }
}
