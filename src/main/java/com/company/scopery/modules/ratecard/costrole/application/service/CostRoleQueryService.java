package com.company.scopery.modules.ratecard.costrole.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.costrole.application.query.SearchCostRoleQuery;
import com.company.scopery.modules.ratecard.costrole.application.response.CostRoleResponse;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardSortFields;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CostRoleQueryService {

    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;

    public CostRoleQueryService(CostRoleRepository costRoleRepository,
                                RateCardAuthorizationService authorizationService) {
        this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public CostRoleResponse get(UUID id) {
        var role = costRoleRepository.findById(id)
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(id));
        authorizationService.requireCostRoleView(role.workspaceId());
        return CostRoleResponse.from(role);
    }

    @Transactional(readOnly = true)
    public PageResult<CostRoleResponse> search(SearchCostRoleQuery query) {
        authorizationService.requireCostRoleView(query.workspaceId());
        CostRoleScope scope = RateCardEnumParser.parseOptional(
                CostRoleScope.class, query.scope(), "COST_ROLE_INVALID_SCOPE", "scope");
        CostRoleStatus status = RateCardEnumParser.parseOptional(
                CostRoleStatus.class, query.status(), "VALIDATION_ERROR", "status");
        PageQuery pageQuery = new PageQuery(query.page(), query.size(), RateCardSortFields.CREATED_AT, false);
        return costRoleRepository.search(scope, query.organizationId(), query.workspaceId(),
                        status, query.category(), query.code(), pageQuery)
                .map(CostRoleResponse::from);
    }
}
