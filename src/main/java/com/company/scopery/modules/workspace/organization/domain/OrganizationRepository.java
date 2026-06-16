package com.company.scopery.modules.workspace.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

    Organization save(Organization organization);

    Optional<Organization> findById(UUID id);

    boolean existsByCode(OrganizationCode code);

    Page<Organization> findAll(String keyword, UUID ownerUserId, OrganizationStatus status, Pageable pageable);
}
