package com.company.scopery.modules.workspace.organization.application.action;

import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceLifecycleService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateOrganizationAction {

    private final OrganizationRepository organizationRepository;
    private final IamAuthResourceLifecycleService authResourceLifecycleService;
    private final WorkspaceActivityLogger activityLogger;

    public ActivateOrganizationAction(OrganizationRepository organizationRepository,
                                       IamAuthResourceLifecycleService authResourceLifecycleService,
                                       WorkspaceActivityLogger activityLogger) {
        this.organizationRepository = organizationRepository;
        this.authResourceLifecycleService = authResourceLifecycleService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrganizationResponse execute(UUID id) {
        Organization org = findOrThrow(id);
        Organization activated = org.activate();
        Organization saved = organizationRepository.save(activated);
        authResourceLifecycleService.activateByRef(saved.id(), IamResourceType.ORGANIZATION);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.ACTIVATE_ORGANIZATION,
                "Organization activated: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    private Organization findOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
