package com.company.scopery.modules.workspace.organization.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.valueobject.OrganizationCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

    Organization save(Organization organization);

    Optional<Organization> findById(UUID id);

    boolean existsByCode(OrganizationCode code);

    List<Organization> findAllByIds(List<UUID> ids);

    PageResult<Organization> findAll(String keyword, UUID ownerUserId, OrganizationStatus status, PageQuery pageQuery);
}
