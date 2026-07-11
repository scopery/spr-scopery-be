package com.company.scopery.modules.workspace.organization.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.organization.application.query.SearchOrganizationQuery;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationQueryService {

    private final OrganizationRepository organizationRepository;

    public OrganizationQueryService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getOrganization(UUID id) {
        return OrganizationResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResult<OrganizationResponse> searchOrganizations(SearchOrganizationQuery query) {
        OrganizationStatus status = WorkspaceEnumParser.parseOptional(
                OrganizationStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_ORGANIZATION_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), WorkspaceSortFields.CREATED_AT, false);
        return organizationRepository.findAll(query.keyword(), query.ownerUserId(), status, pageQuery)
                .map(OrganizationResponse::from);
    }

    private Organization findOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
