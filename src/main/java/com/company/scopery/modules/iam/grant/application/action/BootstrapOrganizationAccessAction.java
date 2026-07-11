package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.BootstrapOrganizationAccessCommand;
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

@Component
public class BootstrapOrganizationAccessAction {

    private static final Logger log = LoggerFactory.getLogger(BootstrapOrganizationAccessAction.class);

    private final IamAuthResourceRepository authResourceRepository;
    private final CreateIamOwnerGrantAction createOwnerGrantAction;

    public BootstrapOrganizationAccessAction(IamAuthResourceRepository authResourceRepository,
                                             CreateIamOwnerGrantAction createOwnerGrantAction) {
        this.authResourceRepository = authResourceRepository;
        this.createOwnerGrantAction = createOwnerGrantAction;
    }

    @Transactional
    public java.util.UUID execute(BootstrapOrganizationAccessCommand command) {
        try {
            IamAuthResource resource = authResourceRepository.save(
                    IamAuthResource.createWithOwnership(
                            IamResourceCode.of("ORG_" + command.orgId().toString().replace("-", "").toUpperCase()),
                            IamResourceType.ORGANIZATION,
                            command.orgName(), null,
                            command.orgId(), command.ownerUserId(), command.orgId(), null,
                            IamResourceVisibility.PRIVATE, null));

            createOwnerGrantAction.execute(new CreateIamOwnerGrantCommand(
                    resource.id(), command.ownerUserId(), IamResourceType.ORGANIZATION, "ORGANIZATION", command.orgId()));
            log.info("IAM bootstrap: org={} resource={} owner={}", command.orgId(), resource.id(), command.ownerUserId());
            return resource.id();
        } catch (Exception e) {
            log.error("IAM bootstrap failed: ORGANIZATION orgId={} reason={}", command.orgId(), e.getMessage(), e);
            throw IamExceptions.iamResourceBootstrapFailed("ORGANIZATION", command.orgId(), e.getMessage());
        }
    }
}
