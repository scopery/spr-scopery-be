package com.company.scopery.modules.workspace.organization.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.command.UpdateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.query.SearchOrganizationQuery;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public OrganizationApplicationService(OrganizationRepository organizationRepository,
                                           WorkspaceActivityLogger activityLogger,
                                           CurrentUserAuthorizationService currentUserService,
                                           WorkspaceIamIntegrationService iamIntegrationService) {
        this.organizationRepository = organizationRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationCommand command) {
        UUID ownerUserId = currentUserService.resolveCurrentUser().id();
        OrganizationCode code = OrganizationCode.of(command.code());

        if (organizationRepository.existsByCode(code)) {
            throw WorkspaceExceptions.organizationCodeAlreadyExists(code.value());
        }

        Organization org = Organization.create(command.name(), code, command.description(), ownerUserId);
        Organization saved = organizationRepository.save(org);

        try {
            iamIntegrationService.bootstrapOrganizationAccess(saved.id(), saved.name(), ownerUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("ORGANIZATION", saved.id());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.CREATE_ORGANIZATION,
                "Organization created: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    @Transactional
    public OrganizationResponse updateOrganization(UpdateOrganizationCommand command) {
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

    @Transactional
    public OrganizationResponse activateOrganization(UUID id) {
        Organization org = findOrThrow(id);
        Organization activated = org.activate();
        Organization saved = organizationRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.ACTIVATE_ORGANIZATION,
                "Organization activated: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    @Transactional
    public OrganizationResponse archiveOrganization(UUID id) {
        Organization org = findOrThrow(id);
        Organization archived = org.archive();
        Organization saved = organizationRepository.save(archived);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.ARCHIVE_ORGANIZATION,
                "Organization archived: " + saved.code().value());

        return OrganizationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganization(UUID id) {
        return OrganizationResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<OrganizationResponse> searchOrganizations(SearchOrganizationQuery query) {
        OrganizationStatus status = WorkspaceEnumParser.parseOptional(
                OrganizationStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_ORGANIZATION_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, WorkspaceSortFields.CREATED_AT));
        return organizationRepository.findAll(query.keyword(), query.ownerUserId(), status, pageable)
                .map(OrganizationResponse::from);
    }

    private Organization findOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
