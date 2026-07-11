package com.company.scopery.modules.iam.grant.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IamAccessGrantRepository {

    IamAccessGrant save(IamAccessGrant grant);

    Optional<IamAccessGrant> findById(UUID id);

    boolean existsBySubjectIdAndResourceId(UUID subjectId, UUID resourceId);

    PageResult<IamAccessGrant> findAll(UUID subjectId, UUID resourceId, UUID workspaceId,
                                  IamAccessGrantStatus status, PageQuery pageQuery);

    List<IamAccessGrant> findActiveBySubjectsAndResource(List<IamSubjectType> subjectTypes, List<UUID> subjectIds, UUID resourceId);

    List<IamAccessGrant> findActiveByResource(UUID resourceId);
}
