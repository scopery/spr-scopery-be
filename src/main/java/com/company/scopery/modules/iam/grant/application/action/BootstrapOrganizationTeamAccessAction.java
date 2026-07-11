package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationTeamAccessCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamOwnerGrantCommand;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class BootstrapOrganizationTeamAccessAction {

    private final IamAuthResourceRepository authResourceRepository;
    private final CreateIamOwnerGrantAction createOwnerGrantAction;

    public BootstrapOrganizationTeamAccessAction(IamAuthResourceRepository authResourceRepository,
                                                 CreateIamOwnerGrantAction createOwnerGrantAction) {
        this.authResourceRepository = authResourceRepository;
        this.createOwnerGrantAction = createOwnerGrantAction;
    }

    @Transactional
    public UUID execute(BootstrapOrganizationTeamAccessCommand command) {
        try {
            UUID orgResourceId = authResourceRepository
                    .findByRefIdAndResourceType(command.organizationId(), IamResourceType.ORGANIZATION)
                    .map(IamAuthResource::id)
                    .orElseThrow(() -> IamExceptions.iamResourceBootstrapFailed(
                            "ORGANIZATION", command.organizationId(), "Organization IAM resource is missing"));

            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("TEAM_" + command.teamId().toString().replace("-", "").toUpperCase()),
                            IamResourceType.TEAM,
                            command.teamName(), null,
                            command.teamId(), command.ownerUserId(), command.organizationId(), null,
                            IamResourceVisibility.PRIVATE, orgResourceId));

            createOwnerGrantAction.execute(new CreateIamOwnerGrantCommand(
                    resource.id(), command.ownerUserId(), IamResourceType.TEAM, "TEAM", command.teamId()));
            return resource.id();
        } catch (Exception e) {
            throw IamExceptions.iamResourceBootstrapFailed("TEAM", command.teamId(), e.getMessage());
        }
    }
}
