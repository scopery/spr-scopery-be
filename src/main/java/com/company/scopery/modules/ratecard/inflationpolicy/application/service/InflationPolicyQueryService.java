package com.company.scopery.modules.ratecard.inflationpolicy.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.inflationpolicy.application.query.SearchInflationPolicyQuery;
import com.company.scopery.modules.ratecard.inflationpolicy.application.response.InflationPolicyResponse;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyScope;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.enums.InflationPolicyStatus;
import com.company.scopery.modules.ratecard.inflationpolicy.domain.model.InflationPolicyRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InflationPolicyQueryService {
    private final InflationPolicyRepository repository;
    private final RateCardAuthorizationService authorizationService;

    public InflationPolicyQueryService(InflationPolicyRepository repository,
                                       RateCardAuthorizationService authorizationService) {
        this.repository = repository; this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public InflationPolicyResponse get(UUID id) {
        var policy = repository.findById(id).orElseThrow(() -> RateCardExceptions.inflationNotFound(id));
        authorizationService.requireInflationView(policy.workspaceId());
        return InflationPolicyResponse.from(policy);
    }

    @Transactional(readOnly = true)
    public PageResult<InflationPolicyResponse> search(SearchInflationPolicyQuery query) {
        authorizationService.requireInflationView(query.workspaceId());
        InflationPolicyScope scope = RateCardEnumParser.parseOptional(InflationPolicyScope.class, query.scope(),
                "INFLATION_POLICY_INVALID_SCOPE", "scope");
        InflationPolicyStatus status = RateCardEnumParser.parseOptional(InflationPolicyStatus.class, query.status(),
                "VALIDATION_ERROR", "status");
        return repository.search(scope, query.organizationId(), query.workspaceId(), status, query.code(),
                new PageQuery(query.page(), query.size(), "createdAt", false)).map(InflationPolicyResponse::from);
    }
}
