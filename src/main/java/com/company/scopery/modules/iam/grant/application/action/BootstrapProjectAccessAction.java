package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapProjectAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamOwnerGrantCommand;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class BootstrapProjectAccessAction {

    private static final Logger log = LoggerFactory.getLogger(BootstrapProjectAccessAction.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final CreateIamOwnerGrantAction createOwnerGrantAction;

    public BootstrapProjectAccessAction(
            IamAuthResourceRepository authResourceRepository,
            CreateIamOwnerGrantAction createOwnerGrantAction) {
        this.authResourceRepository = authResourceRepository;
        this.createOwnerGrantAction = createOwnerGrantAction;
    }

    @Transactional
    public UUID execute(BootstrapProjectAccessCommand command) {
        try {
            var existing = authResourceRepository.findByRefIdAndResourceType(
                    command.projectId(), IamResourceType.PROJECT);
            if (existing.isPresent()) {
                return existing.get().id();
            }

            UUID wsResourceId = authResourceRepository
                    .findByRefIdAndResourceType(command.workspaceId(), IamResourceType.WORKSPACE)
                    .map(IamAuthResource::id)
                    .orElseThrow(() -> IamExceptions.iamResourceBootstrapFailed(
                            "PROJECT",
                            command.projectId(),
                            "Workspace IAM resource missing for " + command.workspaceId()));

            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of(
                                    "PRJ_" + command.projectId().toString().replace("-", "").toUpperCase()),
                            IamResourceType.PROJECT,
                            command.projectName(),
                            null,
                            command.projectId(),
                            command.ownerUserId(),
                            command.organizationId(),
                            command.workspaceId(),
                            IamResourceVisibility.PRIVATE,
                            wsResourceId));

            if (command.ownerUserId() != null) {
                createOwnerGrantAction.execute(new CreateIamOwnerGrantCommand(
                        resource.id(),
                        command.ownerUserId(),
                        IamResourceType.PROJECT,
                        "PROJECT",
                        command.projectId()));
            }

            log.info("IAM bootstrap: project={} resource={} owner={}",
                    command.projectId(), resource.id(), command.ownerUserId());
            return resource.id();
        } catch (Exception e) {
            log.error("IAM bootstrap failed: PROJECT projectId={} reason={}",
                    command.projectId(), e.getMessage(), e);
            throw IamExceptions.iamResourceBootstrapFailed("PROJECT", command.projectId(), e.getMessage());
        }
    }
}
