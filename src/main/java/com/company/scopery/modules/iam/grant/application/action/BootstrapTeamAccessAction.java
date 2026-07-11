package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapTeamAccessCommand;
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
public class BootstrapTeamAccessAction {

    private static final Logger log = LoggerFactory.getLogger(BootstrapTeamAccessAction.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final CreateIamOwnerGrantAction createOwnerGrantAction;

    public BootstrapTeamAccessAction(IamAuthResourceRepository authResourceRepository,
                                     CreateIamOwnerGrantAction createOwnerGrantAction) {
        this.authResourceRepository = authResourceRepository;
        this.createOwnerGrantAction = createOwnerGrantAction;
    }

    @Transactional
    public UUID execute(BootstrapTeamAccessCommand command) {
        try {
            UUID wsResourceId = authResourceRepository
                    .findByRefIdAndResourceType(command.workspaceId(), IamResourceType.WORKSPACE)
                    .map(IamAuthResource::id)
                    .orElse(null);
            UUID organizationId = wsResourceId == null ? null : authResourceRepository.findById(wsResourceId)
                    .map(IamAuthResource::organizationId).orElse(null);

            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("TEAM_" + command.teamId().toString().replace("-", "").toUpperCase()),
                            IamResourceType.TEAM,
                            command.teamName(), null,
                            command.teamId(), command.ownerUserId(), organizationId, command.workspaceId(),
                            IamResourceVisibility.PRIVATE, wsResourceId));

            createOwnerGrantAction.execute(new CreateIamOwnerGrantCommand(
                    resource.id(), command.ownerUserId(), IamResourceType.TEAM, "TEAM", command.teamId()));
            log.info("IAM bootstrap: team={} resource={} owner={}", command.teamId(), resource.id(), command.ownerUserId());
            return resource.id();
        } catch (Exception e) {
            log.error("IAM bootstrap failed: TEAM teamId={} reason={}", command.teamId(), e.getMessage(), e);
            throw IamExceptions.iamResourceBootstrapFailed("TEAM", command.teamId(), e.getMessage());
        }
    }
}
