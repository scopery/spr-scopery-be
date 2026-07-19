package com.company.scopery.modules.ratecard.costrole.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;

import java.util.Optional;
import java.util.UUID;

public interface CostRoleRepository {
    CostRole save(CostRole role);
    Optional<CostRole> findById(UUID id);
    Optional<CostRole> findByCode(String code);
    boolean existsByScopeAndCode(CostRoleScope scope, UUID organizationId, UUID workspaceId, String code);
    boolean isReferencedByRateLines(UUID costRoleId);
    PageResult<CostRole> search(CostRoleScope scope, UUID organizationId, UUID workspaceId,
                                CostRoleStatus status, String category, String code, PageQuery pageQuery);
}
