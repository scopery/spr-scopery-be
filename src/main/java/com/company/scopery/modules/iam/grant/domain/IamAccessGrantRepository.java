package com.company.scopery.modules.iam.grant.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IamAccessGrantRepository {

    IamAccessGrant save(IamAccessGrant grant);

    Optional<IamAccessGrant> findById(UUID id);

    boolean existsBySubjectIdAndResourceId(UUID subjectId, UUID resourceId);

    Page<IamAccessGrant> findAll(UUID subjectId, UUID resourceId, IamAccessGrantStatus status, Pageable pageable);

    List<IamAccessGrant> findActiveBySubjectsAndResource(List<IamSubjectType> subjectTypes, List<UUID> subjectIds, UUID resourceId);
}
