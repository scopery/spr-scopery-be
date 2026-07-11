package com.company.scopery.modules.iam.right.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;

import java.util.Optional;
import java.util.UUID;

public interface IamRightRepository {
    IamRight save(IamRight right);
    Optional<IamRight> findById(UUID id);
    Optional<IamRight> findByCode(IamRightCode code);
    boolean existsByCode(IamRightCode code);
    PageResult<IamRight> findAll(String keyword, String module, IamRightStatus status, PageQuery pageQuery);
}
