package com.company.scopery.modules.workspace.organization.application.action;

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
public class ArchiveOrganizationAction {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceActivityLogger activityLogger;

    public ArchiveOrganizationAction(OrganizationRepository organizationRepository,
                                      WorkspaceActivityLogger activityLogger) {
        this.organizationRepository = organizationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrganizationResponse execute(UUID id) {
        Organization org = findOrThrow(id);
        Organization archived = org.archive();
        Organization saved = organizationRepository.save(archived);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.ARCHIVE_ORGANIZATION,
                "Organization archived: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    private Organization findOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
