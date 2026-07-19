package com.company.scopery.modules.ratecard.ratecard.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RateCardRepository {
    RateCard save(RateCard card);
    Optional<RateCard> findById(UUID id);
    boolean existsByScopeAndCode(RateCardScope scope, UUID organizationId, UUID workspaceId,
                                 UUID clientId, UUID projectId, String code);
    List<RateCard> findActiveDefaultsByWorkspaceId(UUID workspaceId);
    List<RateCard> findActiveByScope(RateCardScope scope, UUID organizationId, UUID workspaceId);
    PageResult<RateCard> search(RateCardScope scope, UUID organizationId, UUID workspaceId,
                                RateCardStatus status, String currency, String code, PageQuery pageQuery);
}
