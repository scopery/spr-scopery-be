package com.company.scopery.modules.workspace.organization.application.action;

import com.company.scopery.modules.workspace.organization.application.command.UpdateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateOrganizationAction {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceActivityLogger activityLogger;

    public UpdateOrganizationAction(OrganizationRepository organizationRepository,
                                     WorkspaceActivityLogger activityLogger) {
        this.organizationRepository = organizationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrganizationResponse execute(UpdateOrganizationCommand command) {
        Organization org = findOrThrow(command.id());
        if (org.status() == OrganizationStatus.ARCHIVED) {
            throw WorkspaceExceptions.organizationArchivedCannotBeUpdated(org.code().value());
        }
        Organization updated = org.update(command.name(), command.description());
        Organization saved = organizationRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.UPDATE_ORGANIZATION,
                "Organization updated: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    private Organization findOrThrow(java.util.UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
