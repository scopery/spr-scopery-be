package com.company.scopery.modules.ratecard.inflationpolicy.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InflationPolicyRepository {
    InflationPolicy save(InflationPolicy policy);
    Optional<InflationPolicy> findById(UUID id);
    boolean existsByScopeAndCode(InflationPolicyScope scope, UUID organizationId, UUID workspaceId, String code);
    List<InflationPolicy> findActiveCovering(InflationPolicyScope scope, UUID organizationId, UUID workspaceId, LocalDate date);
    PageResult<InflationPolicy> search(InflationPolicyScope scope, UUID organizationId, UUID workspaceId,
                                       InflationPolicyStatus status, String code, PageQuery pageQuery);
}
